package org.seventyeight.web.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.seventyeight.web.Core;
import org.seventyeight.web.nodes.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Importer {
	
	private static Logger logger = LogManager.getLogger(Importer.class);
	
	private Map<Integer, String> userMap = new HashMap<Integer, String>();
	private Map<Integer, String> groupMap = new HashMap<Integer, String>();
	
	
	//@Option(name="-type", required=true)
	private String type;

	private String dbHost = "212.97.132.75:3306/cwolfga_gymnerds";
	
	private String dbUser = "cwolfga_admin", dbPass = "avenger";
	
	public static JsonArray getReturnJsonArray(CloseableHttpResponse response) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		
		String output;
		StringBuilder sb = new StringBuilder();
		while ((output = br.readLine()) != null) {
			sb.append(output);
		}
		
        JsonParser parser = new JsonParser();
        try {
        	return (JsonArray) parser.parse(sb.toString());
        } catch(Exception e) {
        	return null;
        }
	}
	
	public static JsonObject getReturnJsonObject(CloseableHttpResponse response) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		
		String output;
		StringBuilder sb = new StringBuilder();
		while ((output = br.readLine()) != null) {
			sb.append(output);
		}
		
        JsonParser parser = new JsonParser();
        try {
        	return (JsonObject) parser.parse(sb.toString());
        } catch(Exception e) {
        	return null;
        }
	}
	

	
	public static abstract class Action {
		public abstract void act(Connection connection, CloseableHttpClient httpclient) throws SQLException, IOException;
	}
	

	
		

	
		
	public class GroupImport extends Action {

		@Override
		public void act(Connection connection, CloseableHttpClient httpclient) throws SQLException, IOException {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM user_groups");
			
			GroupInserter gi = new GroupInserter(httpclient, userMap, groupMap);
			
			while(rs.next()) {
				String groupName = rs.getString("group_name");
				int groupId = rs.getInt("group_id");
				int ownerId = rs.getInt("owner_id");
				
				// Insert
				GroupInserter.Arguments args = new GroupInserter.Arguments(groupName, groupId, ownerId);
				gi.act(args);
			}
		}	
	}
	
		
	public class UserImport extends Action {
		
		public void act(Connection connection, CloseableHttpClient httpclient) throws SQLException, IOException {
			Statement stmt = connection.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT * FROM users");
			
			UserInserter ui = new UserInserter(httpclient, userMap);
			
		    while(rs.next()) {
		    	String u = rs.getString("username");
		    	String p = rs.getString("password");
		    	String e = rs.getString("email");
		    	int userId = rs.getInt("user_id");
		    	logger.info("Username: {}", u);
		    	
		    	// Checking
		    	CheckUser.Arguments args = new CheckUser.Arguments(u, userId);
		    	boolean exists = new CheckUser(httpclient, userMap).act(args);
		    	if(!exists) {
		    		UserInserter.Arguments a = new UserInserter.Arguments(u, userId, p, e);
		    		ui.act(a);
		    	}   	
		    }
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, ClientProtocolException, IOException {
		Importer i = new Importer();
		CmdLineParser parser = new CmdLineParser(i);
		try {
			parser.parseArgument(args);
			i.execute();
		} catch (CmdLineException e) {
			// handling of wrong arguments
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
		}
	}
	
	public Importer() {
	}
	
	public void execute() throws ClassNotFoundException, SQLException, ClientProtocolException, IOException {
		//UserImport ui = new UserImport("mydb5.surftown.dk:3306/cwolfga_gymnerds", "cwolfga_admin", "avenger");
		
		Class.forName("com.mysql.jdbc.Driver");
		String db = "jdbc:mysql://" + dbHost;
		
		Connection con = DriverManager.getConnection(db, dbUser, dbPass);
		Statement stmt = con.createStatement();
		
		CloseableHttpClient httpclient = HttpClients.createDefault();


		new TruncateDatabases(httpclient).act("");
		
		new UserImport().act(con, httpclient);
		
		new GroupImport().act(con, httpclient);
	}
}
