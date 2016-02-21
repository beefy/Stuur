package hello;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.Console;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class ReceiveMsg {

    private final String receive_id;

    public ReceiveMsg(String receive_id) {
	this.receive_id = receive_id;
    }

    public JSONArray getContent() {
	
	String user = "root";
	String pass = "";

	Console console = System.console();
        char passwordArr[] = console.readPassword("Enter password for "+user+": ");
        pass = new String(passwordArr);

	ResultSet rs = null;
	try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }

	try {
	    Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/crowd_shout",user,pass);
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery("CALL recieve_msg("+receive_id+");");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
	}     

	JSONArray json = new JSONArray();

	try {
	    while(rs.next()) {
	    ResultSetMetaData rsmd = rs.getMetaData();
      	    int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();

      	    for (int i=1; i<numColumns+1; i++) {
        	String column_name = rsmd.getColumnName(i);

        	if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
         	    obj.put(column_name, rs.getArray(column_name));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
         	    obj.put(column_name, rs.getInt(column_name));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
         	    obj.put(column_name, rs.getBoolean(column_name));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
         	    obj.put(column_name, rs.getBlob(column_name));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
         	    obj.put(column_name, rs.getDouble(column_name)); 
       		} else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
         	    obj.put(column_name, rs.getFloat(column_name));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
         	    obj.put(column_name, rs.getInt(column_name));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
         	    obj.put(column_name, rs.getNString(column_name));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
         	    obj.put(column_name, rs.getString(column_name));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
         	    obj.put(column_name, rs.getInt(column_name));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
         	    obj.put(column_name, rs.getInt(column_name));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
         	    obj.put(column_name, rs.getDate(column_name));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
        	    obj.put(column_name, rs.getTimestamp(column_name));   
        	} else{
         	    obj.put(column_name, rs.getObject(column_name));
        	}
      	    }

      	    json.put(obj);
	    }
	} catch (JSONException ex) {
	    // do something
	} catch (SQLException ex) {
	    // do something
	}

	return json;
    }
}
