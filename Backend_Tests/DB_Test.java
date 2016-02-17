import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.Console;


public class DB_Test {
	
    public static String get_pass(String user) {

	Console console = System.console();
	char passwordArr[] = console.readPassword("Enter password for "+user+": ");
	return new String(passwordArr);

    }

    public static Connection establish_connection() {
	String user = "root";
        String pass = get_pass(user);

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }

        try {
            return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/crowd_shout",user,pass);
   	} catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return null;
	}

    }

    public static void main(String[] args) {
        
	try {
	    Connection conn = establish_connection();
            Statement stmt = conn.createStatement();
	    ResultSet rs = stmt.executeQuery("SELECT * FROM user_tbl");
	    while(rs.next()) {
		int id = rs.getInt("user_id");
		String user_key = rs.getString("user_key");
		int num_profanity = rs.getInt("num_profanity");
		boolean is_offensive = rs.getInt("is_offensive") == 1;
		boolean has_new_msg = rs.getInt("has_new_msg") == 1;	
		System.out.format("%s, %s, %s, %s, %s\n", id, user_key, num_profanity, is_offensive, has_new_msg);
	    }

	    // close after reading from result set
            stmt.close();
	    conn.close();
	
	} catch (SQLException ex) {
	    System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
	}

	System.out.println("Done");
    }
}
