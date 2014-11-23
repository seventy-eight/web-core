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

public class ConversationInserter extends HTTPAction<ConversationInserter.Arguments, String> {

	private static Logger logger = LogManager.getLogger(ConversationInserter.class);
	
	public ConversationInserter(CloseableHttpClient httpClient, Context context) {
		super(httpClient, context);
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
	public String act(Arguments argument) throws IOException, ImportException {
		String url = "http://localhost:8080/conversations/create";
		HttpPost postRequest = new HttpPost(url);
		
		JsonObject request = getJsonRequest();
		request.addProperty("title", argument.title);
		
		request.addProperty("owner", context.getUserMap().get(argument.ownerId));
		
		StringEntity input = new StringEntity(request.toString());
		input.setContentType("application/json");
		postRequest.setEntity(input);
		CloseableHttpResponse response = httpClient.execute(postRequest);
		
		JsonObject result = Importer.getReturnJsonObject(response);
		logger.debug("RESULT: " + result);
		if(result != null && result.has("identifier")) {
			return result.get("identifier").getAsString();
		} else {
			throw new ImportException("Not a valid result, " + result.toString());
		}
	}	
}
