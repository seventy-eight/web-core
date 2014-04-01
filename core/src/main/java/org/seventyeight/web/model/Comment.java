package org.seventyeight.web.model;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.markup.HtmlGenerator;
import org.seventyeight.markup.SimpleParser;
import org.seventyeight.web.Core;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.utilities.DocumentFinder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Comment extends AbstractNode<Comment> {

    private static Logger logger = LogManager.getLogger( Comment.class );

    private static SimpleParser textParser = new SimpleParser( new HtmlGenerator() );

    public static final String TITLE_FIELD = "title";
    public static final String TEXT_FIELD = "text";
    public static final String USER_FIELD = "owner";
    public static final String DATE_FIELD = "date";
    public static final String RESOURCE_FIELD = "resource";
    public static final String PARENT_FIELD = "parent";

    public static final String COMMENTS_COLLECTION = "comments";

    public Comment(Node parent, MongoDocument document) {
        super(parent, document);
    }

    @Override
    public void updateNode( CoreRequest request ) {
        String text = request.getValue( "comment", "" );
        setText( text );
    }

    protected void setTextParserVersion( String version ) {
        document.set( "textParserVersion", version );
    }

    public String getText( TextType type ) {
        MongoDocument texts = document.getSubDocument( TEXT_FIELD, null );
        if( texts == null || texts.isNull() ) {
            return "";
        } else {
            return texts.get( type.name(), "" );
        }
    }

    /**
     * Get the HTML version of the text
     */
    public String getText() {
        return getText( TextType.html );
    }

    public String getMarkUp() {
        return getText( TextType.markUp );
    }


    /**
     * Assuming the text field is created
     */
    public void setText( String text, TextType type ) {
        logger.debug( "Setting text for " + this );

        MongoDocument texts = document.getSubDocument( TEXT_FIELD, null );
        if( texts != null && !texts.isNull() ) {
            texts.set( type.name(), text );
        } else {
            throw new IllegalStateException( TEXT_FIELD + " field was not found!" );
        }
    }

    public Comment setText(String text) {
        MongoDocument texts = document.getSubDocument( TEXT_FIELD, null );
        if( texts == null ) {
            logger.debug( "Creating text field" );
            texts = new MongoDocument();
            document.set( TEXT_FIELD, texts );
        }

        StringBuilder output = textParser.parse( text );

        // Set the version of the parser and generator
        String version = textParser.getVersion() + ":" + textParser.getGeneratorVersion();
        setTextParserVersion( version );

        setText( text, TextType.markUp );
        setText( output.toString(), TextType.html );

        return this;
    }

    /*
    public static List<Comment> getCommentsByUser(User user, int offset, int number, Node parent) {
        MongoDBQuery query = new MongoDBQuery().is( "type", "comment" ).is( "owner", user.getIdentifier() );
        MongoDocument sort = new MongoDocument().set( "created", 1 );
        List<MongoDocument> docs = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( query, offset, number, sort );

        List<Comment> comments = new ArrayList<Comment>( docs.size() );

        for(MongoDocument d : docs) {
            Comment c = new Comment(parent, d);
            comments.add( Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( c, "view.vm" ) );
        }

        return comments;
    }
    */

    public static class CommentDescriptor extends NodeDescriptor<Comment> {

        @Override
        public String getDisplayName() {
            return "Comment";
        }

        @Override
        public String getType() {
            return "comment";
        }

        @Override
        public Comment newInstance( CoreRequest request, Node parent ) throws ItemInstantiationException {
            Comment comment = super.newInstance( request, parent );

            if(parent instanceof PersistedNode) {
                comment.getDocument().set( RESOURCE_FIELD, ((AbstractNode)parent).getIdentifier() );
                comment.getDocument().set( PARENT_FIELD, ((AbstractNode)parent).getIdentifier() );
            }

            return comment;
        }
    }
}
