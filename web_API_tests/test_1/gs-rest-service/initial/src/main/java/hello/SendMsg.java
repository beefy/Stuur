package hello;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.Console;

public class SendMsg {

    private final String msg_text;
    private final String sending_id;

    public SendMsg(String msg_text, String sending_id) {
        this.msg_text = msg_text;
	this.sending_id = sending_id;
    }

    public String getContent() {
	
	String user = "root";
	String pass = "";

	Console console = System.console();
        char passwordArr[] = console.readPassword("Enter password for "+user+": ");
        pass = new String(passwordArr);

	try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }

	try {
	    Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/crowd_shout",user,pass);
            Statement stmt = conn.createStatement();
            stmt.executeQuery("CALL send_msg('"+msg_text+"', "+sending_id+");");


            // close after reading from result set
            stmt.close();
            conn.close();

	    return "SUCCESS";

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
    		
	    return "ERROR " + ex.getMessage();
	}     
    }
}
