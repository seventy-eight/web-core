package org.seventyeight.web.model;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.utilities.ExtensionUtils;
import org.seventyeight.web.utilities.JsonException;
import org.seventyeight.web.utilities.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class PersistedNode implements Node, Savable, Documented {

    private static Logger logger = LogManager.getLogger( PersistedNode.class );

    public static final String EXTENSIONS = "extensions";

    protected MongoDocument document;

    public PersistedNode() {

    }

    public PersistedNode( MongoDocument document ) {
        this.document = document;
    }

    public final void update(CoreRequest request) throws ClassNotFoundException, ItemInstantiationException {
        logger.debug( "Updating {}", this );

        // Update extensions given a json object
        JsonObject json = null;
        try {
            json = JsonUtils.getJsonFromRequest( request );
            List<JsonObject> objs = JsonUtils.getJsonObjects( json );
            if( !objs.isEmpty() ) {
                updateExtensions( request, objs.get( 0 ) );
            }
        } catch( JsonException e ) {
            logger.debug( "No json provided", e.getMessage() );
        }

        // Default fields
        String title = request.getValue( "title", null );
        if(title != null) {
            setField( "title", title );
        } else {
            throw new IllegalArgumentException( "Title not provided" );
        }

        // Update fields
        updateNode( request );

        // Update user + revision
        update( request.getUser() );
    }

    /**
     * Update the {@link AbstractNode}'s extensions given a {@link CoreRequest} and a {@link JsonObject}. <br/>
     * The method should not save the node, merely update.
     */
    public final void updateExtensions(CoreRequest request, JsonObject json) throws ItemInstantiationException, ClassNotFoundException {
        logger.debug( "Updating extensions for {}", this );

        // Extension from json object
        if( json != null ) {
            logger.debug( "Handling json extension" );
            //handleJsonConfigurations( request, json );
            ExtensionUtils.retrieveExtensions( request, json, this );
        }
    }

    /**
     * Update the {@link AbstractNode}'s fields given a {@link CoreRequest}. <br/>
     * The method should not save the node, merely update.
     */
    public abstract void updateNode(CoreRequest request);

    public List<AbstractExtension> getExtensions() {

        return new ArrayList<AbstractExtension>();
    }

    protected void setDocument( MongoDocument document ) {
        this.document = document;
    }

    public static MongoDocument getSubDocument( MongoDocument document, String type, Class<?> clazz ) {
        MongoDocument extensionClassDocument = document.getr( EXTENSIONS, type, clazz.getCanonicalName() );

        /*
        for( MongoDocument doc : docs ) {
            if( doc.get( "class", null ) != null ) {
                return doc;
            }
        }
        */

        MongoDocument d = new MongoDocument(  );
        extensionClassDocument.addToList( type, d );
        return d;
    }

    /**
     * Get a list of {@link AbstractExtension}s
     */
    public List<AbstractExtension> getExtensions( Class<?> extensionClass ) {
        MongoDocument extensionClassDocument = document.get( EXTENSIONS );
        List<MongoDocument> docs = extensionClassDocument.get( extensionClass.getName() );
        List<AbstractExtension> extensions = new ArrayList<AbstractExtension>( docs.size() );

        for( MongoDocument doc : docs ) {
            try {
                extensions.add( (AbstractExtension) Core.getInstance().getSubDocument( doc ) );
            } catch( ItemInstantiationException e ) {
                logger.error( e );
            }
        }

        return extensions;
    }

    public void addExtension( AbstractExtension extension ) {
        logger.debug( "Adding extension " + extension );
        logger.debug( "Adding extension " + extension.getDocument() );

        document.addToList( "extensions", extension.getDocument() );
    }


    public <T> T getField( String key, T def ) {
        if( document.get( key ) == null ) {
            return def;
        } else {
            return (T) document.get( key );
        }
    }

    public <T> T getField( String key ) {
        if( document.get( key ) == null ) {
            throw new IllegalStateException( "Field " + key + " does not exist" );
        } else {
            return (T) document.get( key );
        }
    }

    public <T> void setField( String key, T value ) {
        document.set( key, value );
    }

    public void update( User owner ) {
        /* Default implementation is no op */
    }


    public static <T extends PersistedNode> T getSubDocument( MongoDocument document ) throws ItemInstantiationException {
        return Core.getInstance().getSubDocument( document );
    }

    @Override
    public MongoDocument getDocument() {
        return document;
    }
}
