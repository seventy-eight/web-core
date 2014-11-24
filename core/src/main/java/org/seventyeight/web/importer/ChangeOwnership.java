package org.seventyeight.web.importer;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.model.Comment;

import com.google.gson.JsonObject;

public class ChangeOwnership extends HTTPAction<ChangeOwnership.Arguments, Boolean> {
	
	private static Logger logger = LogManager.getLogger(ChangeOwnership.class);

	public static class Arguments {
		public String resource;
		public String user;
		
		public Arguments(String resource, String user) {
			this.resource = resource;
			this.user = user;
		}
	}

	public ChangeOwnership(CloseableHttpClient httpClient, Context context) {
		super(httpClient, context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Boolean act(Arguments argument) throws IOException, ImportException {
		JsonObject json = HTTPAction.getJsonRequest();
		json.addProperty("newOwner", argument.user);

		HttpPost postRequest = getPostRequest("resource/" + argument.resource + "/chown", json);
		
		CloseableHttpResponse response = httpClient.execute(postRequest);
		
		JsonObject result = Importer.getReturnJsonObject(response);
		logger.debug("CHOWN: " + result);
		if(result != null) {
			return true;
		} else {
			throw new ImportException("Not a valid result, " + result.toString());
		}
	}
}
