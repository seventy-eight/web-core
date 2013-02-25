package org.seventyeight.web.model;

import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.utilities.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 *         Date: 16-02-13
 *         Time: 23:14
 */
public abstract class AbstractModelObject implements ModelObject {

    private static Logger logger = Logger.getLogger( AbstractModelObject.class );

    public static final String EXTENSIONS = "extensions";

    protected MongoDocument document;

    public AbstractModelObject( MongoDocument document ) {
        this.document = document;
    }

    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        logger.debug( "Begin saving" );

        Saver saver = getSaver( request );

        saver.save();

        logger.debug( "Handling extensions" );
        handleJsonConfigurations( request, jsonData );

        update();

        /* Persist */
        MongoDBCollection.get( Core.getInstance().getDescriptor( getClass() ).getCollectionName() ).save( document );
    }

    public Saver getSaver( CoreRequest request ) {
        return new Saver( this, request );
    }

    public static class Saver {
        protected AbstractModelObject modelObject;
        protected CoreRequest request;

        public Saver( AbstractModelObject modelObject, CoreRequest request ) {
            this.modelObject = modelObject;
            this.request = request;
        }

        public AbstractModelObject getModelObject() {
            return modelObject;
        }

        public void save() throws SavingException {

        }
    }

    public void handleJsonConfigurations( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException {
        logger.debug( "Handling extension class Json data" );

        List<JsonObject> extensionsObjects = JsonUtils.getJsonObjects( jsonData, JsonUtils.JsonType.extensionClass );
        logger.debug( "I got " + extensionsObjects.size() + " extension types" );

        for( JsonObject obj : extensionsObjects ) {
            handleJsonExtensionClass( request, obj );
        }
    }

    public void handleJsonExtensionClass( CoreRequest request, JsonObject extensionConfiguration ) throws ClassNotFoundException, ItemInstantiationException {
        String extensionClassName = extensionConfiguration.get( JsonUtils.__JSON_CLASS_NAME ).getAsString();
        logger.debug( "Extension class name is " + extensionClassName );

        /* Get Json configuration objects */
        List<JsonObject> configs = JsonUtils.getJsonObjects( extensionConfiguration );
        logger.debug( "I got " + configs.size() + " configurations" );

        document.setList( EXTENSIONS );
        for( JsonObject c : configs ) {
            Describable d = handleJsonConfiguration( request, c );
            document.addToList( EXTENSIONS, d.getDocument() );
        }
    }


    public Describable handleJsonConfiguration( CoreRequest request, JsonObject jsonData ) throws ItemInstantiationException, ClassNotFoundException {
        /* Get Json configuration object class name */
        String cls = jsonData.get( JsonUtils.__JSON_CLASS_NAME ).getAsString();
        logger.debug( "Configuration class is " + cls );

        Class<?> clazz = Class.forName( cls );
        Descriptor<?> d = Core.getInstance().getDescriptor( clazz );
        logger.debug( "Descriptor is " + d );

        Describable e = d.newInstance();

        /* Remove data!? */
        if( d.doRemoveDataItemOnConfigure() ) {
            logger.debug( "This should remove the data attached to this modelObject" );
        }

        return e;

    }


    /**
     * Get a list of {@link AbstractExtension}s
     */
    public List<AbstractExtension> getExtensions() throws ItemInstantiationException {
        List<MongoDocument> docs = document.getList( EXTENSIONS );
        List<AbstractExtension> extensions = new ArrayList<AbstractExtension>( docs.size() );

        for( MongoDocument doc : docs ) {
            extensions.add( (AbstractExtension) Core.getInstance().getItem( doc ) );
        }

        return extensions;
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

    public void update() {
        /* Default implementation is no op */
    }


    public static <T extends AbstractModelObject> T getItem( MongoDocument document ) throws ItemInstantiationException {
        return Core.getInstance().getItem( document );
    }

    @Override
    public MongoDocument getDocument() {
        return document;
    }
}
