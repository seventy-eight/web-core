package org.seventyeight.web.model;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.markup.HtmlGenerator;
import org.seventyeight.markup.SimpleParser;
import org.seventyeight.web.Core;
import org.seventyeight.web.nodes.User;

import java.util.Date;

/**
 * @author cwolfgang
 */
public class Comment extends AbstractNode<Comment> {

    private static Logger logger = LogManager.getLogger( Comment.class );

    private static SimpleParser textParser = new SimpleParser( new HtmlGenerator() );

    public static final String TITLE_FIELD = "title";
    public static final String TEXT_FIELD = "text";
    public static final String USER_FIELD = "user";
    public static final String DATE_FIELD = "date";
    public static final String RESOURCE_FIELD = "resource";
    public static final String PARENT_FIELD = "parent";

    public static final String COMMENTS_COLLECTION = "comments";

    public Comment(Node parent, MongoDocument document) {
        super(parent, document);
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        /* Implementation is a no op */
    }

    public static Comment create(Resource<?> resource, User user, AbstractNode<?> parent, String title, String text) throws ItemInstantiationException {

        CommentDescriptor cd = Core.getInstance().getDescriptor( Comment.class );
        Comment instance = cd.newInstance( title, resource );

        instance.getDocument().set( USER_FIELD, user.getIdentifier() );
        instance.getDocument().set( DATE_FIELD, new Date() );
        instance.getDocument().set( RESOURCE_FIELD, resource.getIdentifier() );
        instance.getDocument().set( PARENT_FIELD, parent.getIdentifier() );

        instance.setText( text );

        instance.save();

        return instance;
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
        public String getCollectionName() {
            return COMMENTS_COLLECTION;
        }
    }
}
