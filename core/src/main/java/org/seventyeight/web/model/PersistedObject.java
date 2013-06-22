package org.seventyeight.web.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.nodes.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class PersistedObject extends Actionable implements Savable, Documented {

    private static Logger logger = Logger.getLogger( PersistedObject.class );

    public static final String EXTENSIONS = "extensions";

    protected MongoDocument document;

    public PersistedObject( MongoDocument document ) {
        this.document = document;
    }

    public List<AbstractExtension> getExtensions() {

        return new ArrayList<AbstractExtension>();
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

    public void update( User owner ) {
        /* Default implementation is no op */
    }


    public static <T extends PersistedObject> T getSubDocument( MongoDocument document ) throws ItemInstantiationException {
        return Core.getInstance().getSubDocument( document );
    }

    @Override
    public MongoDocument getDocument() {
        return document;
    }
}
