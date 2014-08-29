package org.seventyeight.web.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.ExtensionGroup;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.utilities.ExtensionUtils;
import org.seventyeight.web.utilities.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cwolfgang
 */
public abstract class PersistedNode implements Node, Savable, Documented {

    private static Logger logger = LogManager.getLogger( PersistedNode.class );

    public static final String EXTENSIONS = "extensions";

    protected MongoDocument document;

    /** Access to the current core/system */
    protected Core core;

    Map<Class<? extends AbstractExtension<?>>, Extension<? extends PersistedNode>> extensions = new HashMap<Class<? extends AbstractExtension<?>>, Extension<? extends PersistedNode>>(  );

    public PersistedNode( Core core, MongoDocument document ) {
        this.document = document;
        this.core = core;
    }

    public Core getCore() {
        return core;
    }

    public MongoDocument resolveExtension(AbstractExtension.ExtensionDescriptor<?> descriptor) {
        logger.debug( "Resolving extension for {}", descriptor );
        logger.info( "DOCUMENT IS {}", document );
        if(descriptor != null) {
            return descriptor.getExtensionDocument( this );
        }

        return null;
    }

    public boolean hasExtension(AbstractExtension.ExtensionDescriptor<?> descriptor) {
        logger.debug( "HAS EXTENSION {}", descriptor );
        MongoDocument doc = resolveExtension( descriptor );
        logger.debug( "EXTENSION DOC = {}", doc );
        return !(doc == null || doc.isNull() || doc.get( "class", null ) == null);
    }

    public MongoDocument getExtension(Class<? extends AbstractExtension<?>> extensionClass) {
        logger.debug( "Resolving extension for {}", extensionClass );
        logger.debug( "THE DOX IS {}", document );
        MongoDocument doc = document.getr2(EXTENSIONS, Descriptor.getJsonId( extensionClass.getName() ));
        logger.debug( "DOC: {}", doc );

        if(doc != null && !doc.isNull()) {
            return doc;
        } else {
           return null;
        }
    }

    public final void updateExtensions(JsonObject json) throws ClassNotFoundException, ItemInstantiationException {
        logger.debug( "Updating {}", this );

        // Update given a json object
        List<JsonObject> objs = JsonUtils.getJsonObjects( json );
        if( !objs.isEmpty() ) {
            //updateExtensions( request, objs.get( 0 ) );
            //document.setList( "extensions" );
            Map<String, MongoDocument> extensions = new HashMap<String, MongoDocument>(  );
            for(JsonObject o : objs) {
                logger.debug( "JSON OBJECT: {}", o );
                ExtensionGroup extensionGroup = ExtensionUtils.getExtensionGroup( core, o );
                if(extensionGroup.getType() == ExtensionGroup.Type.one) {
                    logger.debug( "Single configurations" );

                    JsonObject jsonConfiguration = ExtensionUtils.getJsonConfiguration( o );
                    Descriptor<?> descriptor = ExtensionUtils.getDescriptor( core, jsonConfiguration );
                    if(descriptor != null && descriptor instanceof AbstractExtension.ExtensionDescriptor) {
                        Describable<?> describable = ExtensionUtils.getDescribable( core, (AbstractExtension.ExtensionDescriptor) descriptor, this, jsonConfiguration );
                        if(describable != null) {
                            extensions.put( ( (AbstractExtension.ExtensionDescriptor) descriptor ).getExtensionClassJsonId(), describable.getDocument() );
                        }
                    }

                } else {
                    logger.debug( "Multiple configurations" );

                    JsonArray jsonElements = ExtensionUtils.getConfigurations( o );
                    String jsonId = null;
                    //List<MongoDocument> describableDocuments = new ArrayList<MongoDocument>(  );
                    MongoDocument describables = new MongoDocument();
                    for(JsonElement e : jsonElements) {
                        JsonObject jsonConfiguration = ExtensionUtils.getJsonConfiguration( e.getAsJsonObject() );
                        if(jsonConfiguration != null) {
                            Descriptor<?> descriptor = ExtensionUtils.getDescriptor( core, jsonConfiguration );
                            if(descriptor != null && descriptor instanceof AbstractExtension.ExtensionDescriptor) {
                                Describable<?> describable = ExtensionUtils.getDescribable( core, (AbstractExtension.ExtensionDescriptor) descriptor, this, jsonConfiguration );
                                jsonId = ( (AbstractExtension.ExtensionDescriptor) descriptor ).getExtensionClassJsonId();
                                if(describable != null) {
                                    //describableDocuments.add( describable.getDocument() );
                                    describables.set( descriptor.getJsonId(), describable.getDocument() );
                                }
                            }
                        }

                    }

                    if(jsonId != null) {
                        extensions.put( jsonId, describables );
                    }
                }
            }

            document.set( EXTENSIONS, extensions );

            logger.fatal( "------> {}", document );
        }
    }

    /**
     * Update the {@link AbstractNode}'s extensions given a {@link CoreRequest} and a {@link JsonObject}. <br/>
     * The method should not save the node, merely update.
     */
    /*
    public final void updateExtensions(CoreRequest request, JsonObject json) throws ItemInstantiationException, ClassNotFoundException {
        logger.debug( "Updating extensions for {}", this );

        // Extension from json object
        if( json != null ) {
            logger.debug( "Handling json extension" );
            //handleJsonConfigurations( request, json );
            ExtensionUtils.retrieveExtensions( request, json, this );
        }
    }
    */

    public List<AbstractExtension> getExtensions() {

        return new ArrayList<AbstractExtension>();
    }

    protected void setDocument( MongoDocument document ) {
        this.document = document;
    }

    /**
     * @deprecated
     */
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
    /*
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
    */

    /*
    public void addExtension( AbstractExtension extension ) {
        logger.debug( "Adding extension " + extension );
        logger.debug( "Adding extension " + extension.getDocument() );

        document.addToList( "extensions", extension.getDocument() );
    }
    */


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

    /*
    public static <T extends PersistedNode> T getSubDocument( MongoDocument document ) throws ItemInstantiationException {
        return Core.getInstance().getSubDocument( document );
    }
    */

    @Override
    public MongoDocument getDocument() {
        return document;
    }
    
    public <T extends Action<T>> Action<T> getActionByName(String name) throws ItemInstantiationException {
    	return core.getActionByName(this, name);
    }
}
