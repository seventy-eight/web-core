package org.seventyeight.web.nodes;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.markup.HtmlGenerator;
import org.seventyeight.markup.SimpleParser;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;

/**
 * Basic {@link Resource} for texts.
 *
 * @author cwolfgang
 */
public class Post extends Resource<Post> {

    private static Logger logger = Logger.getLogger( Post.class );

    private static SimpleParser textParser = new SimpleParser( new HtmlGenerator() );

    public static final String TEXT_FIELD = "text";

    public enum TextType {
        markUp,
        html
    }

    public Post( Node parent, MongoDocument document ) {
        super( parent, document );
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

    @Override
    public String getDisplayName() {
        return getTitle();
    }

    @Override
    public String getMainTemplate() {
        return Core.MAIN_TEMPLATE;
    }

    @Override
    public String getPortrait() {
        return "/theme/notepad-small.png";
    }


    public static Post create( String title, Request.Language language, String text, User owner ) throws ItemInstantiationException {
        logger.debug( "Creating post " + title + " for " + owner );
        PostDescriptor d = Core.getInstance().getDescriptor( Post.class );
        Post post = d.newInstance( title );
        post.setMandatoryFields( owner );

        logger.debug( "Creating text" );
        post.setText( text );

        return post;
    }

    /**
     * Set the text as mark up and html.
     * @param text
     */
    public void setText( String text ) {
        MongoDocument texts = document.getSubDocument( TEXT_FIELD, null );
        if( texts == null ) {
            logger.debug( "Creating text field" );
            texts = new MongoDocument();
            document.set( TEXT_FIELD, texts );
        }

        StringBuilder output = textParser.parse( text );
        setText( text, TextType.markUp );
        setText( output.toString(), TextType.html );
    }

    public static Post getPostByTitle( Node parent, String title ) {
        MongoDocument docs = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).findOne( new MongoDBQuery().is( "title", title ) );

        if( docs != null ) {
            try {
                return Core.getInstance().getItem( parent, docs );
            } catch( ItemInstantiationException e ) {
                logger.error( e );
                return null;
            }
        } else {
            logger.debug( "The post " + title + " was not found" );
            return null;
        }
    }

    public static class PostDescriptor extends ResourceDescriptor<Post> {

        @Override
        public String getType() {
            return "post";
        }

        @Override
        public Node getChild( String name ) throws NotFoundException {
            /* Id first */
            try {
                return Core.getInstance().getNodeById( this, name );
            } catch( Exception e ) {
                /* Not an id */
                logger.debug( "Id not found", e );
            }

            Post post = getPostByTitle( this, name );
            if( post != null ) {
                return post;
            } else {
                throw new NotFoundException( "The post " + name + " was not found" );
            }
        }

        @Override
        public String getDisplayName() {
            return "Post";
        }
    }

    public static class Posts implements Node {

        @PostMethod
        public void doCreate( Request request, Response response ) throws ItemInstantiationException, IOException {
            String title = request.getValue( "title", "" );
            String text = request.getValue( "text", "" );

            Post post = Post.create( title, request.getLanguage(), text, request.getUser() );
            post.save();

            response.sendRedirect( "/post/" + post.getIdentifier() + "/" );
        }

        @Override
        public Node getParent() {
            return Core.getInstance();
        }

        @Override
        public String getDisplayName() {
            return "Posts";
        }

        @Override
        public String getMainTemplate() {
            return null;  /* Implementation is a no op */
        }
    }
}
