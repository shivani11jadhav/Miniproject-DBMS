package ff;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/DashboardServlet"})
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Map<String, String>> trendingList = new ArrayList<>();
        List<Map<String, String>> allList = new ArrayList<>();
        
        try (Connection con = DBConnection.getConnection()) {
            // 1. Trending Query: JOINing on user_id (Table column match)
            String trendingSql = "SELECT ideas.*, users.name FROM ideas JOIN users ON ideas.user_id = users.id ORDER BY votes DESC LIMIT 3";
            PreparedStatement ps1 = con.prepareStatement(trendingSql);
            ResultSet rs1 = ps1.executeQuery();
            while(rs1.next()) {
                Map<String, String> idea = new HashMap<>();
                idea.put("id", rs1.getString("id")); // "idea_id" ki jagah "id"
                idea.put("title", rs1.getString("title"));
                idea.put("owner", rs1.getString("name"));
                idea.put("votes", rs1.getString("votes"));
                trendingList.add(idea);
            }

            // 2. All Ideas Query: Sorted by id DESC
            String allSql = "SELECT ideas.*, users.name FROM ideas JOIN users ON ideas.user_id = users.id ORDER BY id DESC";
            PreparedStatement ps2 = con.prepareStatement(allSql);
            ResultSet rs2 = ps2.executeQuery();
            while(rs2.next()) {
                Map<String, String> idea = new HashMap<>();
                idea.put("id", rs2.getString("id")); // "idea_id" ki jagah "id"
                idea.put("title", rs2.getString("title"));
                idea.put("desc", rs2.getString("description"));
                idea.put("owner", rs2.getString("name"));
                idea.put("votes", rs2.getString("votes"));
                allList.add(idea);
            }

            request.setAttribute("trendingIdeas", trendingList);
            request.setAttribute("allIdeas", allList);
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            // Error handling ke liye
            response.getWriter().println("Error loading dashboard: " + e.getMessage());
        }
    }
}