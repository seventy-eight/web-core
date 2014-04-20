package org.seventyeight.web.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.ResourceExtension;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.utilities.JsonException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class NodeDescriptor<T extends AbstractNode<T>> extends Descriptor<T> implements Node, Getable<T> {

    private static Logger logger = LogManager.getLogger( NodeDescriptor.class );

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    /*
    public T newInstance( String title ) throws ItemInstantiationException {
        return newInstance( title, this );
    }
    */

    @Override
    public T newInstance( CoreRequest request, Node parent ) throws ItemInstantiationException {
        String title = request.getValue( "title" );
        if(title == null) {
            throw new IllegalArgumentException( "Title must be provided" );
        }

        return newInstance( request, parent, title );
    }

    public T newInstance( CoreRequest request, Node parent, String title ) throws ItemInstantiationException {
        logger.debug( "New instance of " + getType() + " with title " + title + "(" + allowIdenticalNaming() + ")" );
        if( !allowIdenticalNaming() ) {
            if( titleExists( title, getType() ) ) {
                throw new ItemInstantiationException( "Multiple instances of " + getType() + " with the same title is not allowed." );
            }
        }

        T node = create( title, parent );

        node.getDocument().set( "type", getType() );
        node.getDocument().set( "title", title );

        // TODO possibly have a system user account
        setOwner( request, node );

        /* Save */
        //MongoDBCollection.get( getCollectionName() ).save( node.getDocument() );

        return node;
    }

    protected void setOwner( CoreRequest request, T node ) {
        if(request.getUser() != null) {
            node.getDocument().set( "owner", request.getUser().getIdentifier() );
        }
    }

    @PostMethod
    public void doCreate( Request request, Response response ) throws ItemInstantiationException, IOException, ClassNotFoundException, JsonException {
        String title = request.getValue( "title", null );
        if( title != null ) {
            logger.debug( "Creating " + title );
            T instance = newInstance(request, this);
            instance.update( request );
            instance.save();
            response.sendRedirect( instance.getUrl() );
        } else {
            throw new ItemInstantiationException( "No title provided" );
        }
    }

    @Override
    public List<Class> getExtensionClasses() {
        List<Class> extensions = new ArrayList<Class>( 1 );
        extensions.add( ResourceExtension.class );
        return extensions;
    }

    private boolean titleExists( String title, String type ) {
        MongoDocument doc = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).findOne( new MongoDBQuery().is( Core.NAME_FIELD, title ).is( "type", type ) );
        return !doc.isNull();
    }

    @Override
    protected T create( String title, Node parent ) throws ItemInstantiationException {
        T instance = super.create( title, parent );

        String id = Core.getInstance().getUniqueName( this );
        instance.getDocument().set( "_id", id );
        instance.getDocument().set( "class", clazz.getName() );
        Date now = new Date();
        instance.getDocument().set( "created", now );
        instance.getDocument().set( "updated", now );
        //document.set( "updated", now );
        instance.getDocument().set( "revision", 0 );

        return instance;
    }

    @Override
    public String getCollectionName() {
        return Core.NODES_COLLECTION_NAME;
    }

    public abstract String getType();

    /**
     * Determine whether to allow identical names or not.<br />
     * Default is true.
     */
    public boolean allowIdenticalNaming() {
        return true;
    }

    @Override
    public String getMainTemplate() {
        return "org/seventyeight/web/main.vm";
    }

    @Override
    public T get( String token ) throws NotFoundException {
        logger.debug( "Getting " + token );

        /* First, get by id */
        try {
            if( Integer.parseInt( token ) > 0 ) {
                return Core.getInstance().getNodeById( this, getType() + "-" + token );
            }
        } catch( Exception e ) {
            logger.debug( "the id " + token + " for " + getType() + " does not exist, " + e.getMessage() );
        }

        try {
            return Core.getInstance().getNodeById( this, token );
        } catch( Exception e ) {
            logger.debug( "the id " + token + " does not exist, " + e.getMessage() );
        }

        /* Get resource by title */
        T node = AbstractNode.getNodeByTitle( this, token, getType() );
        if( node != null ) {
            return node;
        } else {
            throw new NotFoundException( "The resource " + token + " was not found" );
        }
    }


    @Override
    public List<String> getApplicableExtensionGroups() {
        List<String> groups = new ArrayList<String>(  );
        groups.add( "Tags" );

        return groups;
    }
}
