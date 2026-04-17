package ff;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/RegisterServlet"})
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Form se data nikaalna
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String pass = request.getParameter("password");

        try {
            // Database se connect karna
            Connection con = DBConnection.getConnection();
            
            if (con != null) {
                // SQL Query taiyar karna
                String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, pass);

                // Query chalana
                int row = ps.executeUpdate();
                
                if (row > 0) {
                    // Success: Login page par bhej do
                    response.sendRedirect("index.html?msg=Registration Success! Please Login.");
                } else {
                    response.getWriter().println("Registration failed. Try again.");
                }
                con.close();
            } else {
                response.getWriter().println("Database Connection Failed!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}