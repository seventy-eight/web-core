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

public class GroupInserter extends HTTPAction<GroupInserter.Arguments, Boolean> {

	private static Logger logger = LogManager.getLogger(GroupInserter.class);
	
	private Map<Integer, String> userMap;
	private Map<Integer, String> groupMap;
	
	public GroupInserter(CloseableHttpClient httpClient,  Map<Integer, String> userMap, Map<Integer, String> groupMap) {
		super(httpClient);
		this.userMap = userMap;
		this.groupMap = groupMap;
	}

	public static class Arguments {
		public String groupName;
		public int groupId;
		public int ownerId;
		
		public Arguments(String groupName, int groupId, int ownerId) {
			this.groupName = groupName;
			this.groupId = groupId;
			this.ownerId = ownerId;
		}
	}

	@Override
	public Boolean act(Arguments argument) throws IOException {
		logger.debug("Adding {}({}) for {}({})", argument.groupName, argument.groupId, userMap.get(argument.ownerId), argument.ownerId);
		
    	JsonObject json = new JsonObject();
    	json.addProperty("title", argument.groupName);
    	json.addProperty("owner", userMap.get(argument.ownerId));

    	JsonObject creds = new JsonObject();
    	creds.addProperty("username", "wolle");
    	creds.addProperty("password", "pass");
    	
    	json.add("credentials", creds);

		HttpPost postRequest = new HttpPost("http://localhost:8080/groups/create");
		StringEntity input = new StringEntity(json.toString());
		input.setContentType("application/json");
		postRequest.setEntity(input);
		CloseableHttpResponse response1 = httpClient.execute(postRequest);
		
		JsonObject result = Importer.getReturnJsonObject(response1);
		logger.debug("REULT: " + result);
		if(result != null && result.has("identifier")) {
			groupMap.put(argument.groupId, result.get("identifier").getAsString());
		}

		return true;
	}
	
}
