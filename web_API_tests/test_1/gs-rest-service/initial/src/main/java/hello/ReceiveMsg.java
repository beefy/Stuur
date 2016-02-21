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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReceiveMsg {

    private final String receive_id;

    public ReceiveMsg(String receive_id) {
	this.receive_id = receive_id;
    }

    public ArrayList<String> getContent() {
	
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

	ArrayList<String> obj = new ArrayList<String>();

	try {
	    while(rs.next()) {
	    ResultSetMetaData rsmd = rs.getMetaData();
      	    int numColumns = rsmd.getColumnCount();

      	    for (int i=1; i<numColumns+1; i++) {
        	String column_name = rsmd.getColumnName(i);

        	if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
         	    obj.add(rs.getArray(column_name).toString());
        	} else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
            	    obj.add(Integer.toString(rs.getInt(column_name)));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
         	    	//boolean
			obj.add(Boolean.toString(rs.getBoolean(column_name)));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
         	    obj.add(rs.getBlob(column_name).toString());
        	} else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
         	    	//double
			obj.add(Double.toString(rs.getDouble(column_name))); 
       		} else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
			//float
			obj.add(Float.toString(rs.getFloat(column_name)));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
		    obj.add(Integer.toString(rs.getInt(column_name)));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
         	    obj.add(rs.getNString(column_name).toString());
        	} else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
         	    obj.add(rs.getString(column_name).toString());
        	} else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
		    obj.add(Integer.toString(rs.getInt(column_name)));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
		    obj.add(Integer.toString(rs.getInt(column_name)));
        	} else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
         	    obj.add(rs.getDate(column_name).toString());
        	} else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
        	    obj.add(rs.getTimestamp(column_name).toString());   
        	} else{
         	    obj.add(rs.getObject(column_name).toString());
        	}
      	    }
	    }
	} catch (JSONException ex) {
	    // do something
	} catch (SQLException ex) {
	    // do something
	}


	return obj;
    }
}
