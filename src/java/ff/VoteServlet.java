package ff;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "VoteServlet", urlPatterns = {"/VoteServlet"})
public class VoteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        // Check karo ki user logged in hai ya nahi
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.html");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String ideaIdStr = request.getParameter("id");

        if (ideaIdStr != null) {
            int ideaId = Integer.parseInt(ideaIdStr);

            try (Connection con = DBConnection.getConnection()) {
                // 1. Pehle check karo ki is user ne is idea ko pehle vote diya hai?
                String checkSql = "SELECT * FROM votes WHERE user_id = ? AND idea_id = ?";
                PreparedStatement psCheck = con.prepareStatement(checkSql);
                psCheck.setInt(1, userId);
                psCheck.setInt(2, ideaId);
                ResultSet rs = psCheck.executeQuery();

               /* if (!rs.next()) {
                    // 2. Agar entry NAHI mili, matlab pehla vote hai. Entry dalo!
                    String insertSql = "INSERT INTO votes (user_id, idea_id) VALUES (?, ?)";
                    PreparedStatement psInsert = con.prepareStatement(insertSql);
                    psInsert.setInt(1, userId);
                    psInsert.setInt(2, ideaId);
                    psInsert.executeUpdate();

                    // 3. Ideas table mein count update karo
                    String updateSql = "UPDATE ideas SET votes = votes + 1 WHERE id = ?";
                    PreparedStatement psUpdate = con.prepareStatement(updateSql);
                    psUpdate.setInt(1, ideaId);
                    psUpdate.executeUpdate();
                }*/
               // ... baaki code upar ka same rahega ...

                if (!rs.next()) {
                    // 2. Agar entry NAHI mili, matlab pehla vote hai. Entry dalo!
                    // Ye INSERT statement hi tumhare MySQL Trigger ko fire (activate) karega
                    String insertSql = "INSERT INTO votes (user_id, idea_id) VALUES (?, ?)";
                    PreparedStatement psInsert = con.prepareStatement(insertSql);
                    psInsert.setInt(1, userId);
                    psInsert.setInt(2, ideaId);
                    psInsert.executeUpdate();

                    /* 3. ISNE AB ZARURAT NAHI HAI (TRIGGER HANDLE KAREGA)
                       Tumne terminal mein jo Trigger banaya hai woh is INSERT ke hote hi
                       apne aap ideas table ka count badha dega.
                    
                    String updateSql = "UPDATE ideas SET votes = votes + 1 WHERE id = ?";
                    PreparedStatement psUpdate = con.prepareStatement(updateSql);
                    psUpdate.setInt(1, ideaId);
                    psUpdate.executeUpdate();
                    */
                }
// ... baaki code niche ka same rahega ...
                // Agar pehle se vote kiya hai, toh bina kuch kiye seedha dashboard par bhej do
                response.sendRedirect("DashboardServlet");

            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().println("Error: " + e.getMessage());
            }
        }
    }
}