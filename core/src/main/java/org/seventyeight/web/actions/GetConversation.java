package org.seventyeight.web.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.DeletingParent;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.model.Parent;
import org.seventyeight.web.nodes.Conversation;

import com.google.gson.JsonObject;

public class GetConversation extends Action<GetConversation> implements Parent, DeletingParent {
	
	private static Logger logger = LogManager.getLogger(GetConversation.class);

	public GetConversation(Core core, Node parent, MongoDocument document) {
		super(core, parent, document);
	}

	@Override
	public String getMainTemplate() {
		return null;
	}

	@Override
	public void updateNode(JsonObject jsonData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void deleteChild(Node node) {
		if(parent != null && parent instanceof DeletingParent) {
            ( (DeletingParent) parent ).deleteChild( node );
        } else {
            logger.debug( "No deleting operation for {}", this );
        }	
	}

	@Override
	public Node getChild(String name) throws NotFoundException {
		try {
			return core.getNodeById(this, name);
		} catch (ItemInstantiationException e) {
			throw new NotFoundException(e.getMessage());
		}
	}

	public static class GetConversationDescriptor extends Action.ActionDescriptor<GetConversation> {

		public GetConversationDescriptor(Core core) {
			super(core);
		}

		@Override
		public String getExtensionName() {
			return "getConversation";
		}

		@Override
		public Class<GetConversation> getExtensionClass() {
			return GetConversation.class;
		}

		@Override
		public String getDisplayName() {
			return "Get conversation";
		}

        @Override
        public boolean isOmnipresent() {
            return true;
        }
	}
}
