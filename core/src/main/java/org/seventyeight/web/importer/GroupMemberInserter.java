package org.seventyeight.web.importer;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

public class GroupMemberInserter extends HTTPAction<GroupMemberInserter.Arguments, Boolean> {

	private static Logger logger = LogManager.getLogger(GroupMemberInserter.class);
	
	public GroupMemberInserter(CloseableHttpClient httpClient) {
		super(httpClient);
	}
	
	public static class Arguments {
		private String groupId;
		private String userId;
		
		public Arguments(String groupId, String userId) {
			this.groupId = groupId;
		}
	}

	@Override
	public Boolean act(Arguments argument) throws IOException {
		logger.debug("Adding members to {}", argument.groupId);
		
		
		HttpPost postRequest = new HttpPost("http://localhost:8080/resource/" + argument.groupId + "/?user=" + argument.userId + "&session=BWAH");
		CloseableHttpResponse response1 = httpClient.execute(postRequest);
		
		JsonObject result = Importer.getReturnJsonObject(response1);
		logger.debug("RESULT: " + result);

		if(response1.getStatusLine().getStatusCode() == 200) {
			logger.debug("Added {} to {}", argument.userId, argument.groupId);
		} else {
			logger.error("Fail to add {} to {}", argument.userId, argument.groupId);
		}
		
		return true;
	}
	
}