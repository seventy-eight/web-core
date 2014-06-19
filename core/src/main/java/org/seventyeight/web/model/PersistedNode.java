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
import org.seventyeight.web.utilities.JsonException;
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

    Map<Class<? extends AbstractExtension<?>>, Extension<? extends PersistedNode>> extensions = new HashMap<Class<? extends AbstractExtension<?>>, Extension<? extends PersistedNode>>(  );

    public PersistedNode( MongoDocument document ) {
        this.document = document;
    }

    public MongoDocument resolveExtension(AbstractExtension.ExtensionDescriptor<?> descriptor) {
        logger.debug( "Resolving extension for {}", descriptor );
        if(descriptor != null) {
            MongoDocument doc = document.getr(EXTENSIONS, descriptor.getExtensionClassJsonId());
            if(descriptor.canHaveMultiple()) {
                logger.debug( "Getting sub extension, {}", doc );
                doc = doc.get( descriptor.getJsonId() );
            }
            logger.debug( "DOC: {}", doc );

            if(doc != null && !doc.isNull() && doc.get( "class", "" ).equals( descriptor.getId() )) {
                return doc;
            }
        }

        return null;
    }

    public boolean hasExtension(AbstractExtension.ExtensionDescriptor<?> descriptor) {
        logger.debug( "HAS EXTENSION {}", descriptor );
        MongoDocument doc = resolveExtension( descriptor );
        return !(doc == null || doc.isNull() || doc.get( "class", null ) == null);
    }

    public MongoDocument getExtension(Class<? extends AbstractExtension<?>> extensionClass) {
        logger.debug( "Resolving extension for {}", extensionClass );

        MongoDocument doc = document.getr(EXTENSIONS, Descriptor.getJsonId( extensionClass.getName() ));
        logger.debug( "DOC: {}", doc );

        if(doc != null && !doc.isNull()) {
            return doc;
        } else {
           return null;
        }
    }

    public final void update(CoreRequest request) throws ClassNotFoundException, ItemInstantiationException {
        logger.debug( "Updating {}", this );

        // Update extensions given a json object
        JsonObject json = null;
        try {
            json = JsonUtils.getJsonFromRequest( request );
            List<JsonObject> objs = JsonUtils.getJsonObjects( json );
            if( !objs.isEmpty() ) {
                //updateExtensions( request, objs.get( 0 ) );
                //document.setList( "extensions" );
                Map<String, MongoDocument> extensions = new HashMap<String, MongoDocument>(  );
                for(JsonObject o : objs) {
                    logger.debug( "JSON OBJECT: {}", o );
                    ExtensionGroup extensionGroup = ExtensionUtils.getExtensionGroup( o );
                    if(extensionGroup.getType() == ExtensionGroup.Type.one) {
                        logger.debug( "Single configurations" );

                        JsonObject jsonConfiguration = ExtensionUtils.getJsonConfiguration( o );
                        Descriptor<?> descriptor = ExtensionUtils.getDescriptor( jsonConfiguration );
                        if(descriptor != null && descriptor instanceof AbstractExtension.ExtensionDescriptor) {
                            Describable<?> describable = ExtensionUtils.getDescribable( (AbstractExtension.ExtensionDescriptor) descriptor, request, this, jsonConfiguration );
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
                            Descriptor<?> descriptor = ExtensionUtils.getDescriptor( jsonConfiguration );
                            if(descriptor != null && descriptor instanceof AbstractExtension.ExtensionDescriptor) {
                                Describable<?> describable = ExtensionUtils.getDescribable( (AbstractExtension.ExtensionDescriptor) descriptor, request, this, jsonConfiguration );
                                jsonId = ( (AbstractExtension.ExtensionDescriptor) descriptor ).getExtensionClassJsonId();
                                if(describable != null) {
                                    //describableDocuments.add( describable.getDocument() );
                                    describables.set( descriptor.getJsonId(), describable.getDocument() );
                                }
                            }

                        }

                        if(jsonId != null) {
                            extensions.put( jsonId, describables );
                        }
                    }

                    /*
                    JsonObject jsonConfiguration = ExtensionUtils.getJsonConfiguration( o );
                    Descriptor<?> descriptor = ExtensionUtils.getDescriptor( jsonConfiguration );
                    if(descriptor != null && descriptor instanceof AbstractExtension.ExtensionDescriptor) {
                        if(( (AbstractExtension.ExtensionDescriptor) descriptor ).canHaveMultiple()) {

                        } else {
                            Describable<?> describable = ExtensionUtils.getDescribable( (AbstractExtension.ExtensionDescriptor) descriptor, request, this, jsonConfiguration );
                            //document.addToList( "extensions", describable.getDocument() );
                            if(describable != null) {
                                //document.set( EXTENSIONS, new MongoDocument().set( ( (AbstractExtension.ExtensionDescriptor) describable.getDescriptor() ).getExtensionClassJsonId(), describable.getDocument() ) );
                                extensions.put( ( (AbstractExtension.ExtensionDescriptor) descriptor ).getExtensionClassJsonId(), describable.getDocument() );
                            }
                        }
                    }
                    Describable<?> describable = ExtensionUtils.handleExtensionConfiguration( request, o, this );
                    //document.addToList( "extensions", describable.getDocument() );
                    if(describable != null && describable.getDescriptor() instanceof AbstractExtension.ExtensionDescriptor) {
                        //document.set( EXTENSIONS, new MongoDocument().set( ( (AbstractExtension.ExtensionDescriptor) describable.getDescriptor() ).getExtensionClassJsonId(), describable.getDocument() ) );
                        extensions.put( ( (AbstractExtension.ExtensionDescriptor) describable.getDescriptor() ).getExtensionClassJsonId(), describable.getDocument() );
                    }
                    */
                }

                document.set( EXTENSIONS, extensions );

                logger.fatal( "------> {}", document );
            }
        } catch( JsonException e ) {
            logger.debug( "No json provided", e.getMessage() );
        }

        // Update fields
        updateNode( request, json );

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
    //public abstract void updateNode(CoreRequest request);

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
