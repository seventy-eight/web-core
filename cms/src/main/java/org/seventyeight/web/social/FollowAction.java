package org.seventyeight.web.social;

import java.io.IOException;
import java.util.Collections;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.GetMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import com.google.gson.JsonObject;

public class FollowAction extends Action<FollowAction> {

	public FollowAction(Core core, Node parent, MongoDocument document) {
		super(core, parent, document);
	}

	@Override
	public String getMainTemplate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateNode(JsonObject jsonData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDisplayName() {
		return "Follow";
	}
	
	/**
	 * 
	 */
	public boolean isFollowing(String id) {
		try {
			User user = core.getNodeById(this, id);
			MongoDocument d = getDocument(user);
			if(d != null && !d.isNull()) {
				return d.get("following", Collections.emptyList()).contains(id);
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
    @GetMethod
    public void doIsFollowing(Request request, Response response) throws IOException {
    	String id = request.getValue("id");
    	response.setContentType("application/json");
    	response.getWriter().print("{\"following\":" + (isFollowing(id) ? "true" : "false") + "}");
    	response.getWriter().flush();
    }
    
    @GetMethod
    public void doFollow(Request request, Response response) {
    	
    }
	
	public static class FollowActionDescriptor extends ActionDescriptor<FollowAction> {

		public FollowActionDescriptor(Core core) {
			super(core);
		}

		@Override
		public boolean isApplicable(Node node) {
			return node instanceof User;
		}

		@Override
		public String getExtensionName() {
			return "follow";
		}

		@Override
		public Class<?> getExtensionClass() {
			return FollowAction.class;
		}

		@Override
		public String getDisplayName() {
			return "Follow";
		}

		@Override
		public boolean isOmnipresent() {
			return true;
		}
	}

}
