package org.seventyeight.web.nodes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.GetMethod;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.Comment;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NodeDescriptor;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.model.PersistedNode;
import org.seventyeight.web.model.Resource;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.utilities.JsonException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Conversation extends Resource<Conversation> {
	
	private static Logger logger = LogManager.getLogger(Conversation.class);
	
	public static final String PARENT_FIELD = "parent";
	public static final String TYPE_NAME = "conversation";

	public Conversation(Core core, Node parent, MongoDocument document) {
		super(core, parent, document);
	}

	@Override
	public void updateNode(JsonObject jsonData) {
		// TODO Auto-generated method stub
	}
	
	public Comment getRootComment() {
        // Find the root comment
        MongoDBQuery query = new MongoDBQuery().is( "conversation", getIdentifier() ).is("parent", getIdentifier()).is( "type", "comment" );
        MongoDocument doc = MongoDBCollection.get( Comment.COMMENTS_COLLECTION ).findOne( query);
        
        return new Comment( core, this, doc );
	}

    @GetMethod
    public void doGetComments(Request request, Response response) throws IOException, TemplateException {
        response.setRenderType( Response.RenderType.NONE );

        // Find the root comment
        MongoDBQuery query = new MongoDBQuery().is( "conversation", getIdentifier() ).is("parent", getIdentifier()).is( "type", "comment" );
        MongoDocument doc = MongoDBCollection.get( Comment.COMMENTS_COLLECTION ).findOne( query);

        //List<String> comments = new ArrayList<String>( docs.size() );
        // A map of lists of comments, keyed by the comments parent id
        Map<String, List<MongoDocument>> comments = new HashMap<String, List<MongoDocument>>();
        
        // 
        Comment c = new Comment( core, this, doc );
       	comments.put(c.getCommentParent(), new ArrayList<MongoDocument>());
        List<MongoDocument> cs = comments.get(c.getCommentParent());
        
        // Place view
        doc.set("view", core.getTemplateManager().getRenderer( request ).renderObject( c, "view.vm" ) );
        cs.add(doc);
        
        // First level replies
        MongoDBQuery flquery = new MongoDBQuery().is( "conversation", getIdentifier() ).is("parent", getIdentifier()).is( "type", "comment" );
        MongoDocument sort = new MongoDocument().set( "created", 1 );
        List<MongoDocument> docs = MongoDBCollection.get( Comment.COMMENTS_COLLECTION ).find( flquery, offset, number, sort );
        
        
        List<String> ids = new ArrayList<String>(docs.size());

        for(MongoDocument d : docs) {
            Comment c = new Comment( core, this, d );
            
            // Make sure the parent key is in the map
            if(!comments.containsKey(c.getCommentParent())) {
            	comments.put(c.getCommentParent(), new ArrayList<MongoDocument>());
            }
            List<MongoDocument> cs = comments.get(c.getCommentParent());
            
            // Place view
            d.set("view", core.getTemplateManager().getRenderer( request ).renderObject( c, "view.vm" ) );
            cs.add(d);
            
            ids.add(c.getIdentifier());
        }
        
        /*
        // Get descendants
        List<String> descendants = getDescendants(ids);
        for(String descendant : descendants) {
        	MongoDocument d = Comment.getComment(descendant);;
        	Comment c = new Comment(core, this, d);
        	
        	// Make sure the parent key is in the map
            if(!comments.containsKey(c.getCommentParent())) {
            	comments.put(c.getCommentParent(), new ArrayList<MongoDocument>());
            }
            List<MongoDocument> cs = comments.get(c.getCommentParent());
            
            // Place view
            d.set("view", core.getTemplateManager().getRenderer( request ).renderObject( c, "view.vm" ) );
            cs.add(d);
        }
        */

        PrintWriter writer = response.getWriter();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        writer.write( gson.toJson( comments ) );
        //writer.write( comments.toString() );
    }
    
    
    /**
     * Given a list comment ids, get their descendants.
     */
    public List<String> getDescendants(List<String> parents) {
    	logger.debug("Getting descendants for {}", parents.toArray());
        MongoDBQuery query = new MongoDBQuery().in("ancestors", parents);
        MongoDocument sort = new MongoDocument().set( "created", 1 );

        List<MongoDocument> docs = MongoDBCollection.get( Comment.COMMENTS_COLLECTION ).find( query, 0, 0, sort, "_id" );
        logger.debug( "DOCS FOR IDS: {}", docs );
        
        List<String> ids = new ArrayList<String>(docs.size());
        
        for(MongoDocument d : docs) {
        	ids.add(d.getIdentifier());
        }
        
        return ids;
    }
    
    public Comment addComment(Request request) throws ClassNotFoundException, ItemInstantiationException {
        String text = request.getValue( "comment", "" );

        Comment.CommentDescriptor descriptor = core.getDescriptor( Comment.class );
        Comment comment = descriptor.newInstance( request, this );

        JsonObject json = request.getJsonField();
        comment.updateConfiguration(json);
        
        // Set the correct conversation
        comment.setConversation(this);
        
        comment.save();
        return comment;
    }

    @PostMethod
    public void doAddComment(Request request, Response response) throws ItemInstantiationException, IOException, TemplateException, ClassNotFoundException, JsonException, NotFoundException {
        response.setRenderType( Response.RenderType.NONE );

        //Comment comment = 
        
        String text = request.getValue( "comment", "" );
        //String title = request.getValue( "commentTitle", "" );

        if(text.length() > 1) {
        	Comment comment = addComment(request);
            setUpdatedCall( null );

            comment.getDocument().set( "view", core.getTemplateManager().getRenderer( request ).renderObject( comment, "view.vm" ) );

            PrintWriter writer = response.getWriter();
            writer.write( comment.getDocument().toString() );
        } else {
            throw new IllegalStateException( "No text provided!" );
        }
    }

    public static long getNumberOfConversations(Resource<?> resource) {
    	MongoDBQuery query = new MongoDBQuery().is(PARENT_FIELD, resource.getIdentifier()).is("type", TYPE_NAME);
    	return MongoDBCollection.get(Core.NODES_COLLECTION_NAME).count(query);
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
