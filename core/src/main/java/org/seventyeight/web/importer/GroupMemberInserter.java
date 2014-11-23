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
	
	public GroupMemberInserter(CloseableHttpClient httpClient, Context context) {
		super(httpClient, context);
	}
	
	public static class Arguments {
		private String group;
		private String user;
		
		public Arguments(String groupId, String userId) {
			this.group = groupId;
		}
	}

	@Override
	public Boolean act(Arguments argument) throws IOException {
		logger.debug("Adding members to {}", argument.group);
		
		
		HttpPost postRequest = new HttpPost("http://localhost:8080/resource/" + argument.group + "/?user=" + argument.user + "&session=BWAH");
		CloseableHttpResponse response1 = httpClient.execute(postRequest);
		
		JsonObject result = Importer.getReturnJsonObject(response1);
		logger.debug("RESULT: " + result);

		if(response1.getStatusLine().getStatusCode() == 200) {
			logger.debug("Added {} to {}", argument.user, argument.group);
		} else {
			logger.error("Fail to add {} to {}", argument.user, argument.group);
		}
		
		return true;
	}
	
}