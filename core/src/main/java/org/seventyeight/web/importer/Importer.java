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
	
	public static abstract class HTTPAction<T extends HTTPAction<T, R>, R> {
		protected R result;
		public abstract T act(CloseableHttpClient httpclient) throws IOException;
		public R getResult() {
			return result;
		}
	}
	
	public static abstract class Action {
		public abstract void act(Connection connection, CloseableHttpClient httpclient) throws SQLException;
	}
	
	public class TruncateDatabases extends HTTPAction<TruncateDatabases, Boolean> {

		@Override
		public TruncateDatabases act(CloseableHttpClient httpclient) throws IOException {
			HttpDelete getRequest = new HttpDelete("http://localhost:8080/clear/");
			CloseableHttpResponse response = httpclient.execute(getRequest);
			if(response.getStatusLine().getStatusCode() == 200) {
				logger.info("Databases truncated");
			} else {
				logger.info("Databases WAS NOT truncated");
			}
			
			return this;
		}
		
	}
	
	public class CheckUser extends HTTPAction<CheckUser, Boolean> {

		private String username;
		private int identifier;
		
		public CheckUser setUsername(String username) {
			this.username = username;
			return this;
		}
		
		public CheckUser setIdentifier(int identifier) {
			this.identifier = identifier;
			return this;
		}
		
		@Override
		public CheckUser act(CloseableHttpClient httpclient) throws IOException {
			String url = "http://localhost:8080/users/getUsers?term=" + URLEncoder.encode(username, "UTF-8") + "&exact=1";
			logger.debug("Checking {} at {}", username, url );
			
			HttpGet getRequest = new HttpGet(url);
			CloseableHttpResponse response = httpclient.execute(getRequest);
			if(response.getStatusLine().getStatusCode() == 200) {
				
				BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
	
				String output;
				StringBuilder sb = new StringBuilder();
				while ((output = br.readLine()) != null) {
					sb.append(output);
				}
				
		        JsonParser parser = new JsonParser();
		        try {
			        JsonArray ja = (JsonArray) parser.parse(sb.toString());
			        
			        for(JsonElement je : ja) {
			        	JsonObject jo = (JsonObject) je;
				        if(jo.has("username") && jo.get("username").getAsString().equalsIgnoreCase(username)) {
				        	logger.debug("{} exists", username);
				        	
				        	userMap.put(identifier, jo.get("identifier").getAsString());
				        	
				        	result = true;
				        	return this;
				        }
			        }
		        } catch(Exception e) {
		        	logger.error(e.getMessage());
		        }
			} else {
				
			}
			
			logger.debug("{} does not exist", username);
			result = false;
			
			return this;
		}	
	}
	
	public class GroupInserter extends HTTPAction<GroupInserter, Boolean> {
		
		private String groupName;
		private int groupId;
		private int ownerId;
		
		public GroupInserter(String groupName, int groupId, int ownerId) {
			this.groupName = groupName;
			this.groupId = groupId;
			this.ownerId = ownerId;
		}

		@Override
		public GroupInserter act(CloseableHttpClient httpclient) throws IOException {
			logger.debug("Adding {}({}) for {}({})", groupName, groupId, userMap.get(ownerId), ownerId);
			
	    	JsonObject json = new JsonObject();
	    	json.addProperty("title", groupName);
	    	json.addProperty("owner", userMap.get(ownerId));

	    	JsonObject creds = new JsonObject();
	    	creds.addProperty("username", "wolle");
	    	creds.addProperty("password", "pass");
	    	
	    	json.add("credentials", creds);

			HttpPost postRequest = new HttpPost("http://localhost:8080/groups/create");
			StringEntity input = new StringEntity(json.toString());
			input.setContentType("application/json");
			postRequest.setEntity(input);
			CloseableHttpResponse response1 = httpclient.execute(postRequest);
			
			JsonObject result = getReturnJsonObject(response1);
			logger.debug("REULT: " + result.toString());
			if(result != null && result.has("identifier")) {
				groupMap.put(groupId, result.get("identifier").getAsString());
			}

			return this;
		}
		
	}
	
	public class GroupImport extends Action {

		@Override
		public void act(Connection connection, CloseableHttpClient httpclient) throws SQLException {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM groups");
			while(rs.next()) {
				String groupName = rs.getString("group_name");
				int ownerId = rs.getInt("owner_id");
				
				// Insert

			}
		}
		
	}
	
	public class UserImport {
		
		private String username;
		private String password;
		private String database;
		
		public UserImport(String database, String username, String password) {
			this.database = database;
			this.username = username;
			this.password = password;
		}
		
		public void userImport() throws SQLException, ClassNotFoundException, ClientProtocolException, IOException {
			Class.forName("com.mysql.jdbc.Driver");
			String db = "jdbc:mysql://" + database;

			Connection con = DriverManager.getConnection(db, username, password);
			Statement stmt = con.createStatement();
			
			CloseableHttpClient httpclient = HttpClients.createDefault();
			
			new TruncateDatabases().act(httpclient);
		    
			ResultSet rs = stmt.executeQuery("SELECT * FROM users");
			
		    while(rs.next()) {
		    	String u = rs.getString("username");
		    	String p = rs.getString("password");
		    	String e = rs.getString("email");
		    	int userId = rs.getInt("user_id");
		    	logger.info("Username: {}", u);
		    	
		    	// Checking
		    	CheckUser cu = new CheckUser().setUsername(u).act(httpclient);
		    	if(!cu.getResult()) {
			    	JsonObject json = new JsonObject();
			    	json.addProperty("username", u);
			    	json.addProperty("title", u);
			    	json.addProperty("password", p);
			    	json.addProperty("password_again", p);

			    	if(e == null || e.isEmpty()) {
			    		e = "noone@example.com";
			    	}
			    	json.addProperty("email", e);
			    	
			    	JsonObject creds = new JsonObject();
			    	creds.addProperty("username", "wolle");
			    	creds.addProperty("password", "pass");
			    	
			    	json.add("credentials", creds);
	
					HttpPost postRequest = new HttpPost("http://localhost:8080/users/create");
					StringEntity input = new StringEntity(json.toString());
					input.setContentType("application/json");
					postRequest.setEntity(input);
					CloseableHttpResponse response1 = httpclient.execute(postRequest);
					
					JsonObject result = getReturnJsonObject(response1);
					logger.debug("REULT: " + result.toString());
					if(result != null && result.has("identifier")) {
						userMap.put(userId, result.get("identifier").getAsString());
					}
				}
		    }
		    
		    httpclient.close();
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
	
	private List<Provider> providers = new ArrayList<Provider>();
	
	public Importer() {
	}
	
	public Importer addProvider(Provider provider) {
		this.providers.add(provider);
		return this;
	}
	
	public void execute() throws ClassNotFoundException, SQLException, ClientProtocolException, IOException {
		//UserImport ui = new UserImport("mydb5.surftown.dk:3306/cwolfga_gymnerds", "cwolfga_admin", "avenger");
		UserImport ui = new UserImport("212.97.132.75:3306/cwolfga_gymnerds", "cwolfga_admin", "avenger");
		ui.userImport();
	}
}
