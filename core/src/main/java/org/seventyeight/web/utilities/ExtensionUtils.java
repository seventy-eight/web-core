package org.seventyeight.web.utilities;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

import java.util.List;

/**
 * @author cwolfgang
 */
public class ExtensionUtils {

    private static Logger logger = LogManager.getLogger( ExtensionUtils.class );

    public static final String EXTENSIONS = "extensions";

    private ExtensionUtils() {

    }

    public static void retrieveExtensions(CoreRequest request, JsonObject json, PersistedNode node) throws ItemInstantiationException, ClassNotFoundException {
        logger.debug( "Retrieving extensions for {} from {}", node, json );

        List<JsonObject> extensionsObjects = JsonUtils.getJsonObjects( json, "extensions" );
        logger.debug( "I got " + extensionsObjects.size() + " extension types" );

        for( JsonObject obj : extensionsObjects ) {
            handleExtensionForClass( request, obj, node );
        }
    }

    /**
     * Given a Json Object, find the
     */
    private static void handleExtensionForClass( CoreRequest request, JsonObject extensionConfiguration, PersistedNode node ) throws ClassNotFoundException, ItemInstantiationException {
        String extensionClassName = extensionConfiguration.get( JsonUtils.__JSON_CLASS_NAME ).getAsString();
        logger.debug( "Extension class name is " + extensionClassName );

        /* Get Json configuration objects */
        List<JsonObject> configs = JsonUtils.getJsonObjects( extensionConfiguration );
        logger.debug( "I got " + configs.size() + " configurations" );

        node.getDocument().setList( EXTENSIONS );
        for( JsonObject c : configs ) {
            Describable d = handleExtensionConfiguration( request, c, node );
            logger.debug( "Created extensions describable: {}", d );
            node.getDocument().addToList( EXTENSIONS, d.getDocument() );
        }
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
    public static Describable handleExtensionConfiguration( CoreRequest request, JsonObject jsonData, PersistedNode node ) throws ItemInstantiationException, ClassNotFoundException {
        // Validate input
        if(jsonData.getAsJsonObject(JsonUtils.__JSON_CONFIGURATION_NAME) == null) {
            throw new IllegalArgumentException( JsonUtils.__JSON_CONFIGURATION_NAME + " was not found" );
        }

        // Get Json configuration object class name
        JsonObject jsonConfiguration = jsonData.getAsJsonObject( JsonUtils.__JSON_CONFIGURATION_NAME );

        if(jsonConfiguration.get(JsonUtils.__JSON_CLASS_NAME) == null) {
            logger.debug( "The field \"class\" was not found" );
            return null;
        }

        String cls = jsonConfiguration.get( JsonUtils.__JSON_CLASS_NAME ).getAsString();
        logger.debug( "Configuration class is " + cls );
        logger.debug( "Json Data for extension configuration: {}", jsonConfiguration );

        Class<?> clazz = Class.forName( cls );
        Descriptor<?> d = Core.getInstance().getDescriptor( clazz );
        logger.debug( "Descriptor is " + d );

        Describable e = d.newInstance( request, node );
        e.updateNode( request, jsonConfiguration );

        /* Remove data!? */
        if( d.doRemoveDataItemOnConfigure() ) {
            logger.debug( "This should remove the data attached to this extension" );
        }

        return e;

    }

}
