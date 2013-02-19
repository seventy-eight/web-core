package org.seventyeight.web.model;

import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.structure.Tuple;
import org.seventyeight.web.Core;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.utilities.JsonException;
import org.seventyeight.web.utilities.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cwolfgang
 *         Date: 16-02-13
 *         Time: 23:14
 */
public abstract class AbstractItem implements Item {

    private static Logger logger = Logger.getLogger( AbstractItem.class );

    public static final String EXTENSIONS = "extensions";

    protected MongoDocument document;

    public AbstractItem( MongoDocument document ) {
        this.document = document;
    }

    public void save( CoreRequest request ) {
        logger.debug( "Begin saving" );

        Saver saver = getSaver( request );

        saver.save();

        try {
            JsonObject jsonData = JsonUtils.getJsonFromRequest( request );

            logger.debug( "Removing actions" );
            recursivelyRemoveActions();

            logger.debug( "Handling extensions" );
            handleJsonConfigurations( request, jsonData );
        } catch( JsonException e ) {
            logger.debug( "No json element: " + e.getMessage() );
        }

        update();
    }

    public Saver getSaver( CoreRequest request ) {
        return new Saver( this, request );
    }

    public static class Saver {
        private AbstractItem item;
        private CoreRequest request;

        public Saver( AbstractItem item, CoreRequest request ) {
            this.item = item;
            this.request = request;
        }

        public AbstractItem getItem() {
            return item;
        }

        public void save() {

        }
    }

    public void handleJsonConfigurations( CoreRequest request, JsonObject jsonData ) {
        logger.debug( "Handling extension class Json data" );

        List<JsonObject> extensionsObjects = JsonUtils.getJsonObjects( jsonData, JsonUtils.JsonType.extensionClass );
        logger.debug( "I got " + extensionsObjects.size() + " extension types" );

        for( JsonObject obj : extensionsObjects ) {
            handleJsonExtensionClass( request, obj );
        }
    }

    public void handleJsonExtensionClass( CoreRequest request, JsonObject extensionConfiguration ) {
        String extensionClassName = extensionConfiguration.get( JsonUtils.__JSON_CLASS_NAME ).getAsString();
        logger.debug( "Extension class name is " + extensionClassName );

        /* Get Json configuration objects */
        List<JsonObject> configs = JsonUtils.getJsonObjects( extensionConfiguration );
        logger.debug( "I got " + configs.size() + " configurations" );

        /* Prepare existing configuration nodes */
        //List<Edge> extensionEdges = node.getEdges( ResourceEdgeType.extension, Direction.OUTBOUND, SeventyEight.FIELD_EXTENSION_CLASS, extensionClassName );
        List<MongoDocument> docs = document.getList( EXTENSIONS );

        Map<String, Tuple<Edge, Node>> nodeMap = new HashMap<String, Tuple<Edge, Node>>();


        for( Edge edge : extensionEdges ) {
            Node node = edge.getTargetNode();
            String className = node.get( "class" );

            if( className != null ) {
                nodeMap.put( className, new Tuple( edge, node ) );
            }
        }

        for( JsonObject c : configs ) {
            try {
                Describable d = handleJsonConfiguration( request, c, nodeMap );
                /**/
                d.getNode().set( SeventyEight.FIELD_EXTENSION_CLASS, extensionClassName ).save();
            } catch( DescribableException e ) {
                logger.error( e );
            }
        }

        /* Remove */
        logger.debug( "Removing superfluous extensions" );
        for( Tuple<Edge, Node> t : nodeMap.values() ) {
            recursivelyRemoveExtensions( t.getFirst() );
        }

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


    public static <T extends AbstractItem> T getItem( MongoDocument document ) throws ItemInstantiationException {
        return Core.getInstance().getItem( document );
    }

}
