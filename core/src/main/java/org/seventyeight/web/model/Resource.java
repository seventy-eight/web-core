package org.seventyeight.web.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.NodeExtension;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.utilities.JsonException;
import org.seventyeight.web.utilities.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author cwolfgang
 */
public abstract class Resource<T extends Resource<T>> extends AbstractNode<T> implements CreatableNode, Portraitable, Parent {

    public static final String RESOURCES_COLLECTION_NAME = "resources";

    private static Logger logger = Logger.getLogger( Resource.class );

    public Resource( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    /**
     * Determines whether a {@link Resource} exists or not
     */
    public static boolean exists( String id ) {
        MongoDBQuery query = new MongoDBQuery().getId( id );
        return MongoDBCollection.get( RESOURCES_COLLECTION_NAME ).count( query ) > 0;
    }

    @PostMethod
    public void doSetPortrait( Request request, Response response ) throws IOException, JsonException {
        logger.debug( "Setting portrait" );

        JsonObject json = JsonUtils.getJsonFromRequest( request );
        List<JsonObject> objs = JsonUtils.getJsonObjects( json );
        setPortrait( request, objs.get( 0 ) );

        /* Redirect */
        response.sendRedirect( "" );
    }

    public void setPortrait( Request request, JsonObject json ) {
        /* No op */
    }

    @Override
    public ResourceDescriptor<T> getDescriptor() {
        return Core.getInstance().getDescriptor( getClass() );
    }

    @Override
    public List<AbstractExtension> getExtensions() {
        List<AbstractExtension> es = super.getExtensions( NodeExtension.class );
        //es.addAll( Core.getInstance().getExtensions( PermanentExtension.class ) );
        return es;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public Node getChild( String name ) {
        return null;
    }

    @Override
    public String getPortrait() {
        if( false ) {
            return "/theme/framed-question-mark-small.png";
        } else {
            return "/theme/framed-question-mark-small.png";
        }
    }

    protected void setMandatoryFields( User owner ) {
        logger.debug( "The mandatory fields, " + owner );
        setOwner( owner );
    }

    public void doBadge( Request request, Response response ) throws TemplateException, IOException {
        response.getWriter().write( Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( this, "badge.vm" ) );
    }

    public Set<String> getConfiguredExtensionTypes() {
        MongoDocument doc = document.getSubDocument( Core.Relations.EXTENSIONS, null );
        if( doc != null && !doc.isNull()) {
            return doc.getKeys();
        } else {
            return Collections.EMPTY_SET;
        }
    }

    public List<AbstractExtension> getConfiguredExtensions() throws ItemInstantiationException {
        logger.debug( "Getting configured extensions for " + this );
        List<AbstractExtension> extensions = new ArrayList<AbstractExtension>(  );
        for( String type : getConfiguredExtensionTypes() ) {
            logger.debug( "Type: " + type );

            MongoDocument doc = document.getr( Core.Relations.EXTENSIONS, type );
            logger.debug( "Extension document is " + doc );
            for( String e : doc.getKeys() ) {
                logger.debug( "Extension: " + e );

                MongoDocument ext = doc.getSubDocument( e, null );
                if( ext != null && !ext.isNull() ) {
                    AbstractExtension instance = Core.getInstance().getItem( this, ext );
                    extensions.add( instance );
                }
            }
        }

        return extensions;
    }


}
