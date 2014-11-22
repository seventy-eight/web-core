package org.seventyeight.web.importer;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

public class UserInserter extends HTTPAction<UserInserter.Arguments, Boolean> {

	private Map<Integer, String> userMap;
	
	public UserInserter(CloseableHttpClient httpClient, Map<Integer, String> userMap) {
		super(httpClient);
		this.userMap = userMap;
	}

	private static Logger logger = LogManager.getLogger(UserInserter.class);
	
	public static class Arguments {
		public String username;
		public String password;
		public String email;
		public int userId;
		
		public Arguments(String username, int userId, String password, String email) {
			this.username = username;
			this.userId = userId;
			this.password = password;
			this.email = email;
		}
	}

	@Override
	public Boolean act(Arguments argument) throws IOException {
		JsonObject json = new JsonObject();
    	json.addProperty("username", argument.username);
    	json.addProperty("title", argument.username);
    	json.addProperty("password", argument.password);
    	json.addProperty("password_again", argument.password);

    	if(argument.email == null || argument.email.isEmpty()) {
    		argument.email = "noone@example.com";
    	}
    	json.addProperty("email", argument.email);
    	
    	JsonObject creds = new JsonObject();
    	creds.addProperty("username", "wolle");
    	creds.addProperty("password", "pass");
    	
    	json.add("credentials", creds);

		HttpPost postRequest = new HttpPost("http://localhost:8080/users/create");
		StringEntity input = new StringEntity(json.toString());
		input.setContentType("application/json");
		postRequest.setEntity(input);
		CloseableHttpResponse response1 = httpClient.execute(postRequest);
		
		JsonObject result = Importer.getReturnJsonObject(response1);
		logger.debug("REULT: " + result);
		if(result != null && result.has("identifier")) {
			userMap.put(argument.userId, result.get("identifier").getAsString());
		}
		
		return true;
	}
	
}
