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

public class ConversationInserter extends HTTPAction<ConversationInserter.Arguments, Boolean> {

	private static Logger logger = LogManager.getLogger(ConversationInserter.class);
	
	private Map<Integer, String> userMap;
	
	public ConversationInserter(CloseableHttpClient httpClient, Map<Integer, String> userMap) {
		super(httpClient);
		this.userMap = userMap;
	}

	public static class Arguments {
		public String title;
		public int ownerId;
		
		public Arguments(String title, int ownerId) {
			this.title = title;
			this.ownerId = ownerId;
		}
	}

	@Override
	public Boolean act(Arguments argument) throws IOException {
		String url = "http://localhost:8080/conversations/create";
		HttpPost postRequest = new HttpPost(url);
		
		JsonObject request = getJsonRequest();
		request.addProperty("title", argument.title);
		request.addProperty("owner", userMap.get(argument.ownerId));
		
		StringEntity input = new StringEntity(request.toString());
		input.setContentType("application/json");
		postRequest.setEntity(input);
		CloseableHttpResponse response = httpClient.execute(postRequest);
		
		JsonObject result = Importer.getReturnJsonObject(response);
		logger.debug("REULT: " + result);
		if(result != null && result.has("identifier")) {
			userMap.put(argument.userId, result.get("identifier").getAsString());
		}
		
		return true;
	}
	
}
