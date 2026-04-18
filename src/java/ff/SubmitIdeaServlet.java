package ff;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "SubmitIdeaServlet", urlPatterns = {"/SubmitIdeaServlet"})
public class SubmitIdeaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // 1. Session Check (userEmail aur userId dono check kar rahe hain)
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.html");
            return;
        }

        // Tumhare table ke hisaab se hum userId (Integer) use karenge
        Integer userId = (Integer) session.getAttribute("userId");
        String title = request.getParameter("ititle");
        String desc = request.getParameter("idesc");

        if (title == null || title.trim().isEmpty()) {
            response.sendRedirect("DashboardServlet?msg=Title cannot be empty!&status=warning");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                response.sendRedirect("DashboardServlet?msg=Database Connection Failed!&status=danger");
                return;
            }

            // 2. Duplicate Check
            String checkSql = "SELECT title FROM ideas WHERE LOWER(title) = LOWER(?)";
            PreparedStatement psCheck = con.prepareStatement(checkSql);
            psCheck.setString(1, title.trim());
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<script type=\"text/javascript\">");
                out.println("alert('This idea name is already submitted! Please try a different name.');");
                out.println("window.location.href = 'DashboardServlet';");
                out.println("</script>");
            } else {
                // 3. UPDATED INSERT QUERY: Table columns (user_id, title, description, visibility, votes)
                String sql = "INSERT INTO ideas (user_id, title, description, visibility, votes) VALUES (?, ?, ?, 'Public', 0)";
                PreparedStatement ps = con.prepareStatement(sql);
                
                ps.setInt(1, userId); // user_id (INT) set kar rahe hain
                ps.setString(2, title.trim());
                ps.setString(3, desc);

                int result = ps.executeUpdate();
                
                if (result > 0) {
                    response.sendRedirect("DashboardServlet?msg=Idea Posted Successfully!&status=success");
                } else {
                    response.sendRedirect("DashboardServlet?msg=Failed to post idea!&status=danger");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("DashboardServlet?msg=Server Error: " + e.getMessage() + "&status=danger");
        }
    }
}