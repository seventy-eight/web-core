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
public class Topic extends Resource<Topic> {

    private static Logger logger = Logger.getLogger( Topic.class );

    private static SimpleParser textParser = new SimpleParser( new HtmlGenerator() );

    public static final String TEXT_FIELD = "text";

    public enum TextType {
        markUp,
        html
    }

    public Topic( Node parent, MongoDocument document ) {
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

    public static Topic create( String title, Request.Language language, String text, User owner ) throws ItemInstantiationException {
        logger.debug( "Creating topic " + title + " for " + owner );
        TopicDescriptor d = Core.getInstance().getDescriptor( Topic.class );
        Topic topic = d.newInstance( title );
        topic.setMandatoryFields( owner );

        logger.debug( "Creating text" );
        topic.setText( text );

        return topic;
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

    public static Topic getTopicByTitle( Node parent, String title ) {
        MongoDocument docs = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).findOne( new MongoDBQuery().is( "title", title ) );

        if( docs != null ) {
            try {
                return Core.getInstance().getItem( parent, docs );
            } catch( ItemInstantiationException e ) {
                logger.error( e );
                return null;
            }
        } else {
            logger.debug( "The topic " + title + " was not found" );
            return null;
        }
    }

    public static class TopicDescriptor extends ResourceDescriptor<Topic> {

        @Override
        public String getType() {
            return "topic";
        }

        @Override
        public Node getChild( String name ) throws NotFoundException {
            /* Id first */
            try {
                return Core.getInstance().getNodeById( this, name );
            } catch( Exception e ) {
                /* Not an id */
                logger.debug( "The id " + name + " was not found" );
            }

            Topic topic = getTopicByTitle( this, name );
            if( topic != null ) {
                return topic;
            } else {
                throw new NotFoundException( "The topic " + name + " was not found" );
            }
        }

        @Override
        public String getDisplayName() {
            return "Topic";
        }
    }

    public static class Topics implements Node {

        @PostMethod
        public void doCreate( Request request, Response response ) throws ItemInstantiationException, IOException {
            String title = request.getValue( "title", "" );
            String text = request.getValue( "text", "" );

            Topic topic = Topic.create( title, request.getLanguage(), text, request.getUser() );
            topic.save();

            response.sendRedirect( "/topic/" + topic.getIdentifier() + "/" );
        }

        @Override
        public Node getParent() {
            return Core.getInstance();
        }

        @Override
        public String getDisplayName() {
            return "Topics";
        }

        @Override
        public String getMainTemplate() {
            return null;  /* Implementation is a no op */
        }
    }
}
