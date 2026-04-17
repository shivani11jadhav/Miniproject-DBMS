<%@ page import="java.util.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>IdeaHub Dashboard</title>
    <style>
        :root {
            --primary: #6366f1;
            --bg: #f3f4f6;
            --card: #ffffff;
            --text: #1f2937;
            --accent: #4f46e5;
        }

        body { 
            font-family: 'Segoe UI', sans-serif; 
            background-color: var(--bg); 
            margin: 0; color: var(--text);
        }

        .navbar {
            background: linear-gradient(135deg, var(--accent), #7c3aed);
            color: white; padding: 1rem 2rem;
            display: flex; justify-content: space-between; align-items: center;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }

        .container { padding: 2rem; max-width: 1100px; margin: auto; }

        /* --- Trending Section Styling --- */
        .trending-container {
            margin-bottom: 3rem;
        }
        
        .trending-grid {
            display: flex;
            gap: 20px;
            overflow-x: auto;
            padding: 10px 5px;
            scrollbar-width: thin;
        }

        .trending-card {
            min-width: 280px;
            background: linear-gradient(white, #f0f9ff);
            border: 2px solid var(--primary);
            border-radius: 15px;
            padding: 20px;
            box-shadow: 0 4px 12px rgba(99, 102, 241, 0.15);
            position: relative;
        }

        .trending-badge {
            position: absolute; top: -10px; right: 10px;
            background: #fbbf24; color: #92400e;
            padding: 2px 10px; border-radius: 10px; font-weight: bold; font-size: 0.7rem;
        }

        /* --- Normal Grid Styling --- */
        .grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
            gap: 25px;
        }

        .card {
            background: var(--card); border-radius: 12px; padding: 20px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.05);
            border-left: 6px solid var(--primary);
            transition: 0.3s;
        }
        .card:hover { transform: translateY(-5px); }

        .vote-badge {
            background: #d1fae5; color: #065f46;
            padding: 5px 12px; border-radius: 20px; font-weight: bold;
        }

        .btn-pitch {
            background: #10b981; color: white; padding: 12px 24px;
            border-radius: 8px; border: none; font-weight: bold; cursor: pointer;
        }

        .btn-vote {
            text-decoration: none; color: var(--primary); font-weight: bold;
            border: 1px solid var(--primary); padding: 5px 10px; border-radius: 5px;
        }

        /* Modal styling wahi rahegi */
        #modalOverlay {
            display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%;
            background: rgba(0,0,0,0.6); justify-content: center; align-items: center; z-index: 1000;
        }
        .modal-box { background: white; padding: 30px; border-radius: 15px; width: 450px; }
    </style>
</head>
<body>

<div class="navbar">
    <h2>💡 IdeaHub</h2>
    <div>
        <span>Welcome, <b><%= session.getAttribute("userName") %></b></span> | 
        <a href="LogoutServlet" style="color: #ffcfcf; text-decoration: none;">Logout</a>
    </div>
</div>

<div class="container">
    
    <div class="trending-container">
        <h2 style="color: var(--accent);">🔥 Trending Now (Top 3)</h2>
        <div class="trending-grid">
            <% 
                List<Map<String, String>> trending = (List<Map<String, String>>) request.getAttribute("trendingIdeas");
                if (trending != null && !trending.isEmpty()) {
                    for (Map<String, String> tIdea : trending) {
            %>
            <div class="trending-card">
                <div class="trending-badge">TOP RANKED</div>
                <h3 style="margin: 0 0 10px 0;"><%= tIdea.get("title") %></h3>
                <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 15px;">
                    <small>By: <%= tIdea.get("owner") %></small>
                    <span class="vote-badge">⭐ <%= tIdea.get("votes") %></span>
                </div>
            </div>
            <%      } 
                } 
            %>
        </div>
    </div>

    <hr style="border: 0; border-top: 1px solid #ddd; margin: 2rem 0;">

    <div class="header-row">
        <h1>Recent Startup Ideas</h1>
        <button class="btn-pitch" onclick="showModal()">+ Pitch New Idea</button>
    </div>

    <div class="grid">
        <% 
            List<Map<String, String>> ideas = (List<Map<String, String>>) request.getAttribute("allIdeas");
            if (ideas != null) {
                for (Map<String, String> idea : ideas) {
        %>
        <div class="card">
            <h3><%= idea.get("title") %></h3>
            <p><%= idea.get("desc") %></p>
            <div class="card-footer" style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #eee; padding-top: 15px; margin-top: 15px;">
                <small>By: <b><%= idea.get("owner") %></b></small>
                <div>
                    <span style="font-weight: bold; margin-right: 10px;"><%= idea.get("votes") %> Votes</span>
                    <a href="VoteServlet?id=<%= idea.get("id") %>" class="btn-vote">Upvote ▲</a>
                </div>
            </div>
        </div>
        <%      } 
            } 
        %>
    </div>
</div>

<div id="modalOverlay">
    <div class="modal-box">
        <h2>Pitch Your Idea 🚀</h2>
        <form action="SubmitIdeaServlet" method="POST">
            <input type="text" name="ititle" placeholder="Idea Title" style="width: 100%; padding: 10px; margin-bottom: 10px; border-radius: 5px; border: 1px solid #ccc;">
            <textarea name="idesc" rows="5" placeholder="Description" style="width: 100%; padding: 10px; margin-bottom: 10px; border-radius: 5px; border: 1px solid #ccc;"></textarea>
            <button type="submit" class="btn-pitch" style="width: 100%;">Launch Idea</button>
            <button type="button" onclick="hideModal()" style="width: 100%; background: none; border: none; margin-top: 10px; cursor: pointer;">Cancel</button>
        </form>
    </div>
</div>

<script>
    function showModal() { document.getElementById('modalOverlay').style.display = 'flex'; }
    function hideModal() { document.getElementById('modalOverlay').style.display = 'none'; }
</script>

</body>
</html>