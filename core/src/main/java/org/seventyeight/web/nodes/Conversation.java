package org.seventyeight.web.nodes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.GetMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.Comment;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NodeDescriptor;
import org.seventyeight.web.model.PersistedNode;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
	

    @GetMethod
    public void doGetComments(Request request, Response response) throws IOException, TemplateException {
        response.setRenderType( Response.RenderType.NONE );

        int number = request.getInteger( "number", 10 );
        int offset = request.getInteger( "offset", 0 );

        MongoDBQuery query = new MongoDBQuery().is( "conversation", getIdentifier() ).is( "type", "comment" );
        MongoDocument sort = new MongoDocument().set( "created", 1 );
        List<MongoDocument> docs = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( query, offset, number, sort );

        //List<String> comments = new ArrayList<String>( docs.size() );
        Map<String, List<MongoDocument>> comments = new HashMap<String, List<MongoDocument>>();

        for(MongoDocument d : docs) {
            Comment c = new Comment( core, this, d );
            if(!comments.containsKey(c.getCommentParent())) {
            	comments.put(c.getCommentParent(), new ArrayList<MongoDocument>());
            }
            List<MongoDocument> cs = comments.get(c.getCommentParent());
            
            // Place view
            d.set("view", core.getTemplateManager().getRenderer( request ).renderObject( c, "view.vm" ) );
            cs.add(d);
        }

        PrintWriter writer = response.getWriter();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        writer.write( gson.toJson( comments ) );
        //writer.write( comments.toString() );
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
