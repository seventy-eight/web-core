package org.seventyeight.web.utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.ExtensionGroup;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class ExtensionUtils {

    private static Logger logger = LogManager.getLogger( ExtensionUtils.class );

    public static final String EXTENSIONS = "extensions";

    private ExtensionUtils() {

    }

    /*
    public static void retrieveExtensions(CoreRequest request, JsonObject json, PersistedNode node) throws ItemInstantiationException, ClassNotFoundException {
        logger.debug( "Retrieving extensions for {} from {}", node, json );

        List<JsonObject> extensionsObjects = JsonUtils.getJsonObjects( json, "extensions" );
        logger.debug( "I got " + extensionsObjects.size() + " extension types" );

        for( JsonObject obj : extensionsObjects ) {
            handleExtensionForClass( request, obj, node );
        }
    }
    */

    /**
     * Given a Json Object, find the
     */
    /*
    private static void handleExtensionForClass( CoreRequest request, JsonObject extensionConfiguration, PersistedNode node ) throws ClassNotFoundException, ItemInstantiationException {
        String extensionClassName = extensionConfiguration.get( JsonUtils.CLASS_NAME ).getAsString();
        logger.debug( "Extension class name is " + extensionClassName );

        // Get Json configuration objects
        List<JsonObject> configs = JsonUtils.getJsonObjects( extensionConfiguration );
        logger.debug( "I got " + configs.size() + " configurations" );

        node.getDocument().setList( EXTENSIONS );
        for( JsonObject c : configs ) {
            Describable d = handleExtensionConfiguration( request, c, node );
            logger.debug( "Created extensions describable: {}", d );
            node.getDocument().addToList( EXTENSIONS, d.getDocument() );
        }
    }
    */

    public static ExtensionGroup getExtensionGroup(Core core, JsonObject jsonData) {
        if(jsonData.getAsJsonPrimitive( JsonUtils.EXTENSION ) == null) {
            throw new IllegalArgumentException( "The json data does not have the extension field" );
        }

        String extension = jsonData.get( JsonUtils.EXTENSION ).getAsString();
        return core.getExtensionGroup( extension );
    }

    public static JsonArray getConfigurations(JsonObject jsonData) {
        // Validate input
        if(jsonData.getAsJsonArray( JsonUtils.CONFIGURATIONS ) == null) {
            throw new IllegalArgumentException( JsonUtils.CONFIGURATIONS + " was not found" );
        }

        // Get Json configuration object class name
        return jsonData.getAsJsonArray( JsonUtils.CONFIGURATIONS );
    }

    /**
     * Given a json object get the configuration element.
     * @param jsonData
     * @return - the json configuration or null if not found
     */
    public static JsonObject getJsonConfiguration(JsonObject jsonData) {
        // Validate input
        if(jsonData.getAsJsonObject(JsonUtils.CONFIGURATION ) == null) {
            logger.debug( JsonUtils.CONFIGURATION + " was not found" );
            return null;
        }

        // Get Json configuration object class name
        return jsonData.getAsJsonObject( JsonUtils.CONFIGURATION );
    }

    public static Descriptor<?> getDescriptor(Core core, JsonObject jsonConfiguration) throws ClassNotFoundException {
        if(jsonConfiguration.get(JsonUtils.CLASS_NAME) == null) {
            logger.debug( "The field \"class\" was not found" );
            return null;
        }

        String cls = jsonConfiguration.get( JsonUtils.CLASS_NAME ).getAsString();
        logger.debug( "Configuration class is " + cls );
        logger.debug( "Json Data for extension configuration: {}", jsonConfiguration );

        Class<?> clazz = Class.forName( cls );
        Descriptor<?> d = core.getDescriptor( clazz );
        logger.debug( "Descriptor is " + d );

        return d;
    }

    public static Describable<?> getDescribable( Core core, AbstractExtension.ExtensionDescriptor descriptor, PersistedNode node, JsonObject jsonConfiguration ) throws ItemInstantiationException {
        Describable e = descriptor.newInstance( core, node, jsonConfiguration );
        e.updateNode( jsonConfiguration );

        /* Remove data!? */
        if( descriptor.doRemoveDataItemOnConfigure() ) {
            logger.debug( "This should remove the data attached to this extension" );
        }

        return e;
    }

    /**
     * Get a describable given a json object
     * On the form:
     *
     * {
     *  extension: <extension class>,
     *  config: { class: <class>, configuration ... }
     * }
     */
    public static Describable handleExtensionConfiguration( Core core, JsonObject jsonData, PersistedNode node ) throws ItemInstantiationException, ClassNotFoundException {
        // Validate input
        if(jsonData.getAsJsonObject(JsonUtils.CONFIGURATION ) == null) {
            throw new IllegalArgumentException( JsonUtils.CONFIGURATION + " was not found" );
        }

        // Get Json configuration object class name
        JsonObject jsonConfiguration = jsonData.getAsJsonObject( JsonUtils.CONFIGURATION );

        if(jsonConfiguration.get(JsonUtils.CLASS_NAME ) == null) {
            logger.debug( "The field \"class\" was not found" );
            return null;
        }

        String cls = jsonConfiguration.get( JsonUtils.CLASS_NAME ).getAsString();
        logger.debug( "Configuration class is " + cls );
        logger.debug( "Json Data for extension configuration: {}", jsonConfiguration );

        Class<?> clazz = Class.forName( cls );
        Descriptor<?> d = core.getDescriptor( clazz );
        logger.debug( "Descriptor is " + d );

        Describable e = d.newInstance( core, node, jsonConfiguration );
        e.updateNode( jsonConfiguration );

        /* Remove data!? */
        if( d.doRemoveDataItemOnConfigure() ) {
            logger.debug( "This should remove the data attached to this extension" );
        }

        return e;

    }

}
