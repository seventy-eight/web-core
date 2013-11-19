package org.seventyeight.web.nodes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.markup.HtmlGenerator;
import org.seventyeight.markup.SimpleParser;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;

/**
 * Basic {@link Resource} for texts.
 *
 * @author cwolfgang
 */
public class Topic extends Resource<Topic> {

    private static Logger logger = LogManager.getLogger( Topic.class );

    private static SimpleParser textParser = new SimpleParser( new HtmlGenerator() );

    public static final String TEXT_FIELD = "text";

    public enum TextType {
        markUp,
        html
    }

    public Topic( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public Saver getSaver( CoreRequest request ) {
        return new TopicSaver( this, request );
    }

    public class TopicSaver extends Saver {

        public TopicSaver( AbstractNode modelObject, CoreRequest request ) {
            super( modelObject, request );
        }

        @Override
        public void save() throws SavingException {
            String text = request.getValue( "text", null );
            if( text == null || text.isEmpty() ) {
                throw new SavingException( "No text is provided" );
            }
            setText( text );
        }
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

        // Set the version of the parser and generator
        String version = textParser.getVersion() + ":" + textParser.getGeneratorVersion();
        setTextParserVersion( version );

        setText( text, TextType.markUp );
        setText( output.toString(), TextType.html );
    }

    protected void setTextParserVersion( String version ) {
        document.set( "textParserVersion", version );
    }

    public String getTextParserVersion() {
        return document.get( "textParserVersion", "" );
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
        public String getDisplayName() {
            return "Topic";
        }
    }
}
