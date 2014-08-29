package org.seventyeight.web.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.DeletingParent;
import org.seventyeight.web.model.Getable;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.model.Parent;
import org.seventyeight.web.model.Resource;
import org.seventyeight.web.nodes.Conversation;

import com.google.gson.JsonObject;

public class Conversations extends Action<Conversations> implements Getable<Conversation>, DeletingParent {
	
	private static Logger logger = LogManager.getLogger(Conversations.class);

	public Conversations(Core core, Node parent, MongoDocument document) {
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
		return "Conversations()";
	}
	
	@Override
	public void deleteChild(Node node) {
		if(parent != null && parent instanceof DeletingParent) {
            ( (DeletingParent) parent ).deleteChild( node );
        } else {
            logger.debug( "No deleting operation for {}", this );
        }	
	}

	/*
	@Override
	public Node getChild(String name) throws NotFoundException {
		try {
			return core.getNodeById(this, name);
		} catch (ItemInstantiationException e) {
			throw new NotFoundException(e.getMessage());
		}
	}
	*/

	@Override
	public Conversation get(Core core, String token) throws NotFoundException {
		try {
			return core.getNodeById(this, token);
		} catch (ItemInstantiationException e) {
			throw new NotFoundException(e.getMessage());
		}
	}
	
	public long getNumberOfConversations() {
    	MongoDBQuery query = new MongoDBQuery().is(Conversation.PARENT_FIELD, ((AbstractNode<?>) parent).getIdentifier()).is("type", Conversation.TYPE_NAME);
    	return MongoDBCollection.get(Core.NODES_COLLECTION_NAME).count(query);
    }

	public static class ConversationsDescriptor extends Action.ActionDescriptor<Conversations> {

		public ConversationsDescriptor(Core core) {
			super(core);
		}

		@Override
		public String getExtensionName() {
			return "conversations";
		}

		@Override
		public Class<Conversations> getExtensionClass() {
			return Conversations.class;
		}

		@Override
		public String getDisplayName() {
			return "Conversations";
		}

        @Override
        public boolean isOmnipresent() {
            return true;
        }
	}
}
