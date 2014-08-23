package org.seventyeight.web.nodes;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.Comment;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NodeDescriptor;
import org.seventyeight.web.model.PersistedNode;

import com.google.gson.JsonObject;

public class Conversation extends AbstractNode<Conversation> {
	
	public static final String PARENT_FIELD = "parent";

	public Conversation(Core core, Node parent, MongoDocument document) {
		super(core, parent, document);
	}

	@Override
	public void updateNode(JsonObject jsonData) {
		// TODO Auto-generated method stub
		
	}

	public static class ConversationDescriptor extends NodeDescriptor<Conversation> {

		public ConversationDescriptor(Node parent) {
			super(parent);
		}
		
        @Override
        public Conversation newInstance( CoreRequest request, Node parent ) throws ItemInstantiationException {
            Conversation conversation = super.newInstance( request, parent );

            if(parent instanceof PersistedNode) {
                conversation.getDocument().set( PARENT_FIELD, request.getValue("parent") );
            }

            return conversation;
        }

		@Override
		public String getType() {
			return "conversation";
		}

		@Override
		public String getDisplayName() {
			return "Conversation";
		}
		
	}
}
