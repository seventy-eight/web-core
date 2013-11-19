package org.seventyeight.web.utilities;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.model.ParameterRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class JsonUtils {

    private static Logger logger = LogManager.getLogger( JsonUtils.class );

    private JsonUtils() {

    }

    public static JsonObject getJsonFromRequest( ParameterRequest request ) throws JsonException {
        String json = request.getParameter( "json" );
        if( json == null ) {
            throw new JsonException( "Json was null" );
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println( gson.toJson( json ) );

        JsonParser parser = new JsonParser();
        JsonObject jo = (JsonObject) parser.parse( json );
        return jo;
    }


    public static final String __JSON_CONFIGURATION_NAME = "config";
    //public static final String __JSON_EXTENSION_NAME = "extensions";
    public static final String __JSON_CLASS_NAME = "class";

    public enum JsonType {
        config,
        extensionClass
    }

    public static List<JsonObject> getJsonObjects( JsonObject obj ) {
        return getJsonObjects( obj, JsonType.config );
    }

    public static List<JsonObject> getJsonObjects( JsonObject obj, JsonType type ) {
        logger.debug( "Getting " + type + " Json objects" );

        List<JsonObject> objects = new ArrayList<JsonObject>();

        JsonElement configElement = obj.get( type.toString() );

        /**/
        if( configElement != null ) {
            if( configElement.isJsonObject() ) {
                logger.debug( "obj is JsonObject" );
                objects.add( configElement.getAsJsonObject() );
            } else if( configElement.isJsonArray() ) {
                logger.debug( "obj is JsonArray" );
                JsonArray jarray = configElement.getAsJsonArray();

                for( JsonElement e : jarray ) {
                    logger.debug( "e is jsonObject" );
                    if( e.isJsonObject() ) {
                        objects.add( e.getAsJsonObject() );
                    }
                }
            }
        }

        return objects;
    }
}
