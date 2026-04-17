package ff;
import java.sql.*;

public class DBConnection {
    public static Connection getConnection() {
        try {
            // MySQL 5.1 version ke liye ye driver ekdum sahi hai
            Class.forName("com.mysql.jdbc.Driver");
            // FounderFlowDB naam ka database MySQL mein hona chahiye
            // Check karo ki tumhara MySQL password "root" hai ya khali ""
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/FounderFlowDB", "root", "Pass@$HIV11");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}