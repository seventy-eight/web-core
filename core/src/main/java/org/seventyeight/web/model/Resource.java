package org.seventyeight.web.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.authorization.ACL;
import org.seventyeight.web.authorization.AccessControlled;
import org.seventyeight.web.extensions.NodeExtension;
import org.seventyeight.web.extensions.PartitionContributor;
import org.seventyeight.web.extensions.Partitioned;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.utilities.JsonException;
import org.seventyeight.web.utilities.JsonUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author cwolfgang
 */
public abstract class Resource<T extends Resource<T>> extends AbstractNode<T> implements CreatableNode, Portraitable, Parent, Partitioned, AccessControlled {

    public static final String RESOURCES_COLLECTION_NAME = "resources";

    private static Logger logger = LogManager.getLogger( Resource.class );

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
        response.setRenderType( Response.RenderType.NONE );

        JsonObject json = JsonUtils.getJsonFromRequest( request );
        List<JsonObject> objs = JsonUtils.getJsonObjects( json );
        if( !objs.isEmpty() ) {
            setPortrait( request, objs.get( 0 ) );
        }

        /* Redirect */
        response.sendRedirect( "" );
    }

    public void setPortrait( Request request, JsonObject json ) {
        /* No op */
    }

    @Override
    public NodeDescriptor<T> getDescriptor() {
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

    public List<Action.ActionDescriptor<?>> getActions() {

        List<Action.ActionDescriptor<?>> ds = new ArrayList<Action.ActionDescriptor<?>>(  );

        for( Descriptor d : Core.getInstance().getExtensionDescriptors( Action.class ) ) {
            if( (( AbstractExtension.ExtensionDescriptor)d).isApplicable( this ) ) {
                ds.add( (Action.ActionDescriptor<?>) d );
            }
        }

        return ds;
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

    @Override
    public List<ContributingPartitionView> getPartitions( Locale locale ) {
        List<ContributingPartitionView> partitions = new ArrayList<ContributingPartitionView>();
        partitions.add( new ContributingPartitionView( "view", "Main", this ) );

        // Get extensions adding to the list
        for( PartitionContributor pc : Core.getInstance().getExtensions( PartitionContributor.class ) ) {
            pc.insertContributions( partitions );
        }

        return partitions;
    }

    @Override
    public ContributingPartitionView getActivePartition( Request request ) {
        String current = request.getValue( "part", "" );
        if( current.length() > 0 ) {
            return new ContributingPartitionView( current, current, this );
        } else {
            return new ContributingPartitionView( "view", "Main", this );
        }
    }

    @PostMethod
    public void doAddComment(Request request, Response response) throws ItemInstantiationException, IOException, TemplateException {
        response.setRenderType( Response.RenderType.NONE );

        String text = request.getValue( "comment", "" );
        String title = request.getValue( "commentTitle", "" );

        if(text.length() > 1) {
            Comment comment = Comment.create( this, request.getUser(), this, title, text );
            if(comment != null) {
                update( request.getUser(), false );
                save();
            }
        } else {
            throw new IllegalStateException( "No text provided!" );
        }
    }

    public void doGetComments(Request request, Response response) throws IOException, TemplateException {
        response.setRenderType( Response.RenderType.NONE );

        int number = request.getInteger( "number", 10 );
        int offset = request.getInteger( "offset", 0 );

        MongoDBQuery query = new MongoDBQuery().is( "resource", getIdentifier() );
        MongoDocument sort = new MongoDocument().set( "created", 1 );
        List<MongoDocument> docs = MongoDBCollection.get( Comment.COMMENTS_COLLECTION ).find( query, offset, number, sort );

        List<String> comments = new ArrayList<String>( docs.size() );

        for(MongoDocument d : docs) {
            Comment c = new Comment( this, d );
            comments.add( Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( c, "view.vm" ) );
        }

        PrintWriter writer = response.getWriter();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        writer.write( gson.toJson( comments ) );
    }

    @Override
    public ACL getACL() {
        MongoDocument doc = document.get( "acl" );

        //
        if( doc == null || doc.isNull() ) {
            return ACL.ALL_ACCESS;
        } else {
            // TODO
            return ACL.ALL_ACCESS;
        }
    }
}
