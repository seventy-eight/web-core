package org.seventyeight.web.utilities;

import com.google.gson.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.model.ParameterRequest;
import org.seventyeight.web.servlet.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class JsonUtils {

    private static Logger logger = LogManager.getLogger( JsonUtils.class );

    private JsonUtils() {

    }

    public static boolean hasACLExtension(JsonObject json) {
    	if(json == null) {
    		return false;
    	}
    	
    	if(!json.has("extensions") || !json.get("extensions").isJsonArray()) {
    		return false;
    	}
    	
    	for(JsonElement e : json.get("extensions").getAsJsonArray()) {
    		
    	}
    	
    	return true;
    }
    
    public static JsonObject getJsonFromRequest( ParameterRequest request ) throws JsonException {
        String json = request.getParameter( "json" );
        if( json == null ) {
            throw new JsonException( "Json was null" );
        }

        JsonParser parser = new JsonParser();
        JsonObject jo = (JsonObject) parser.parse( json );

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println( gson.toJson( jo ) );

        return jo;
    }
    
    public static JsonObject getJsonRequest(Request request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = request.getReader().readLine()) != null) {
        	sb.append(s);
        }
        
        JsonParser parser = new JsonParser();
        JsonObject jo = (JsonObject) parser.parse( sb.toString() );

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println( gson.toJson( jo ) );

        return jo;
    }


    public static final String CONFIGURATION = "config";
    //public static final String __JSON_EXTENSION_NAME = "extensions";
    public static final String CLASS_NAME = "class";
    public static final String EXTENSION = "extension";
    public static final String CONFIGURATIONS = "configurations";

    public static String get(JsonObject json, String key, String defaultValue) {
        if(json.has( key )) {
            return json.get( key ).getAsString();
        } else {
            return defaultValue;
        }
    }

    public static int get(JsonObject json, String key, int defaultValue) {
        if(json.has( key )) {
            return json.get( key ).getAsInt();
        } else {
            return defaultValue;
        }
    }

    public enum JsonType {
        config,
        extensionClass
    }

    public static List<JsonObject> getJsonObjects( JsonObject obj ) {
        return getJsonObjects( obj, "extensions" );
    }

    public static List<JsonObject> getJsonObjects( JsonObject obj, String type ) {
        logger.debug( "Getting " + type + " Json objects" );

        List<JsonObject> objects = new ArrayList<JsonObject>();

        JsonElement configElement = obj.get( type );

        /**/
        if( configElement != null ) {
            if( configElement.isJsonObject() ) {
                logger.debug( "obj is JsonObject" );
                objects.add( configElement.getAsJsonObject() );
            } else if( configElement.isJsonArray() ) {
                logger.debug( "obj is JsonArray" );
                JsonArray jarray = configElement.getAsJsonArray();

                for( JsonElement e : jarray ) {
                    logger.debug( "e is jsonObject, {}, {}", e, e.isJsonObject() );
                    if( e.isJsonObject() ) {
                        objects.add( e.getAsJsonObject() );
                    }
                }
            }
        }

        return objects;
    }
}
