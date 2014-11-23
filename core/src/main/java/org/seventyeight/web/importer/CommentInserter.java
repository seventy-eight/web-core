package org.seventyeight.web.importer;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.model.Comment;

import com.google.gson.JsonObject;

public class CommentInserter extends HTTPAction<CommentInserter.Arguments, String> {
	
	private static Logger logger = LogManager.getLogger(CommentInserter.class);

	public CommentInserter(CloseableHttpClient httpClient, Context context) {
		super(httpClient, context);
	}
	
	public static class Arguments {
		public String title;
		public String comment;
		public String user;
		public String conversation;
		public String parent;
		
		public Arguments(String conversation, String user, String title, String comment, String parent) {
			this.conversation = conversation;
			this.user = user;
			this.title = title;
			this.comment = comment;
			this.parent = parent;
		}
	}

	@Override
	public String act(Arguments argument) throws IOException, ImportException {
		JsonObject json = HTTPAction.getJsonRequest();
		json.addProperty(Comment.CONVERSATION_FIELD, argument.conversation);
		json.addProperty(Comment.PARENT_FIELD, argument.parent);
		json.addProperty(Comment.TITLE_FIELD, argument.title);
		json.addProperty(Comment.COMMENT_FIELD, argument.comment);
		json.addProperty(Comment.PARENT_FIELD, argument.parent);
		
		logger.debug("REQUEST: {}", json.toString());
		
		HttpPost postRequest = getPostRequest("resource/" + argument.conversation + "/addComment", json);
		
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
