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
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
	
	//@Option(name="-type", required=true)
	private String type;
	
	public static abstract class Action<T> {
		protected T result;
		public abstract <E extends Action<T>> E act(CloseableHttpClient httpclient) throws IOException;
		public T getResult() {
			return result;
		}
	}
	
	public static class CheckUser extends Action<Boolean> {

		private String username;
		
		public CheckUser setUsername(String username) {
			this.username = username;
			return this;
		}
		
		@Override
		public CheckUser act(CloseableHttpClient httpclient) throws IOException {
			String url = "http://localhost:8080/users/getUsers?term=" + URLEncoder.encode(username, "UTF-8") + "&exact=1";
			System.out.println(url);
			HttpGet getRequest = new HttpGet(url);
			CloseableHttpResponse response = httpclient.execute(getRequest);
			System.out.println("----" + response.getStatusLine().getStatusCode() );
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
			        
			        //System.out.println("::::" + ja.toString());
			        
			        for(JsonElement je : ja) {
			        	JsonObject jo = (JsonObject) je;
			        	//System.out.println("---->" + je.toString());
			        	//System.out.println(jo.get("username").getAsString());
			        	//System.out.println(jo.get("username").toString() + "==" + username + "==" + jo.has("username") + "==" + (jo.get("username").toString().equalsIgnoreCase(username)));
				        if(jo.has("username") && jo.get("username").getAsString().equalsIgnoreCase(username)) {
				        	System.out.println("YAY");
				        	result = true;
				        	return this;
				        }
			        }
		        } catch(Exception e) {
		        	System.out.println(e.getMessage());
		        }
			} else {
				
			}
			
			result = false;
			return this;
		}
		
	}
	
	public static class UserImport {
		
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
		    
			ResultSet rs = stmt.executeQuery("SELECT * FROM users");
		    while(rs.next()) {
		    	String u = rs.getString("username");
		    	String p = rs.getString("password");
		    	String e = rs.getString("email");
		    	System.out.println("Username; " + u);
		    	
		    	// Checking
		    	CheckUser cu = new CheckUser().setUsername(u).act(httpclient);
		    	if(!cu.getResult()) {
			    	JsonObject json = new JsonObject();
			    	json.addProperty("username", u);
			    	json.addProperty("title", u);
			    	json.addProperty("password", p);
			    	json.addProperty("password_again", p);
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
					
					BufferedReader br = new BufferedReader(new InputStreamReader((response1.getEntity().getContent())));
	 
					String output;
					System.out.println("Output from Server:");
					while ((output = br.readLine()) != null) {
						System.out.println(response1.getStatusLine() + ", \"" + output + "\"");
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
	
	private Core core;
	
	public Importer() {
	}
	
	public Importer(Core core) {
		this.core = core;
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
