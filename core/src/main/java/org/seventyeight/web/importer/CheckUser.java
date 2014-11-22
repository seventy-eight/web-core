package org.seventyeight.web.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CheckUser extends HTTPAction<CheckUser.Arguments, Boolean> {

	private Map<Integer, String> userMap;

	private static Logger logger = LogManager.getLogger(CheckUser.class);

	public CheckUser(CloseableHttpClient httpClient, Map<Integer, String> userMap) {
		super(httpClient);
		this.userMap = userMap;
	}
	
	public static class Arguments {
		public String username;
		public int identifier;
		
		public Arguments(String username, int identifier) {
			this.username = username;
			this.identifier = identifier;
		}
	}
	
	@Override
	public Boolean act(Arguments argument) throws IOException {
		String url = "http://localhost:8080/users/getUsers?term=" + URLEncoder.encode(argument.username, "UTF-8") + "&exact=1";
		logger.debug("Checking {} at {}", argument.username, url );
		
		HttpGet getRequest = new HttpGet(url);
		CloseableHttpResponse response = httpClient.execute(getRequest);
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
			        if(jo.has("username") && jo.get("username").getAsString().equalsIgnoreCase(argument.username)) {
			        	logger.debug("{} exists", argument.username);
			        	
			        	userMap.put(argument.identifier, jo.get("identifier").getAsString());
			        	
			        	result = true;
			        	return true;
			        }
		        }
	        } catch(Exception e) {
	        	logger.error(e.getMessage());
	        }
		} else {
			
		}
		
		logger.debug("{} does not exist", argument.username);
		result = false;
		
		return false;
	}	
}
