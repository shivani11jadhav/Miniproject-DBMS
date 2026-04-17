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
        
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String title = request.getParameter("ititle");
        String desc = request.getParameter("idesc");

        if (userId == null) {
            response.sendRedirect("index.html");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            // 1. Duplicate Check Logic (Case-Insensitive)
            // LOWER() use karne se 'SMART' aur 'smart' dono same treat honge
            String checkSql = "SELECT title FROM ideas WHERE LOWER(title) = LOWER(?)";
            PreparedStatement psCheck = con.prepareStatement(checkSql);
            psCheck.setString(1, title);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                // Agar idea mil gaya toh JavaScript alert dikhao
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<script type=\"text/javascript\">");
                out.println("alert('This idea name is already submitted! Please try a different name.');");
                out.println("window.location.href = 'DashboardServlet';");
                out.println("</script>");
            } else {
                // 2. Agar unique hai, toh insert karo
                String sql = "INSERT INTO ideas (user_id, title, description) VALUES (?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, userId);
                ps.setString(2, title);
                ps.setString(3, desc);
                ps.executeUpdate();
                
                response.sendRedirect("DashboardServlet");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}