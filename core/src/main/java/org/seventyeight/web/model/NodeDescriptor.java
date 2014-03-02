package org.seventyeight.web.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.ResourceExtension;
import org.seventyeight.web.model.extensions.NodeListener;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.lang.reflect.Constructor;
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

    public T newInstance( String title ) throws ItemInstantiationException {
        return newInstance( title, this );
    }

    @Override
    public T newInstance( String title, Node parent ) throws ItemInstantiationException {
        logger.debug( "New instance of " + getType() + " with title " + title + "(" + allowIdenticalNaming() + ")" );
        if( !allowIdenticalNaming() ) {
            if( titleExists( title, getType() ) ) {
                throw new ItemInstantiationException( "Multiple instances of " + getType() + " with the same title is not allowed." );
            }
        }

        T node = createNode( parent );

        node.getDocument().set( "type", getType() );
        node.getDocument().set( "title", title );

        /* Save */
        MongoDBCollection.get( getCollectionName() ).save( node.getDocument() );

        return node;
    }


    @PostMethod
    public void doCreate( Request request, Response response ) throws ItemInstantiationException, IOException, SavingException, ClassNotFoundException {
        String title = request.getValue( "title", null );
        if( title != null ) {
            logger.debug( "Creating " + title );
            T instance = newInstance( title );
            instance.save( request, null );
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
        MongoDocument doc = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).findOne( new MongoDBQuery().is( Core.NAME_FIELD, title ).is( "type", type ) );
        return !doc.isNull();
    }

    protected T createNode( Node parent ) throws ItemInstantiationException {
        logger.debug( "Creating " + clazz.getName() );

        MongoDBCollection collection = MongoDBCollection.get( getCollectionName() );
        MongoDocument document = new MongoDocument();

        T instance = null;
        try {
            Constructor<T> c = clazz.getConstructor( Node.class, MongoDocument.class );
            instance = c.newInstance( parent, document );
        } catch( Exception e ) {
            throw new ItemInstantiationException( "Unable to instantiate " + clazz.getName(), e );
        }

        String id = Core.getInstance().getUniqueName( this );
        document.set( "_id", id );
        document.set( "class", clazz.getName() );
        Date now = new Date();
        document.set( "created", now );
        //document.set( "updated", now );
        document.set( "revision", 0 );

        return instance;
    }

    @Override
    public String getCollectionName() {
        return Core.RESOURCES_COLLECTION_NAME;
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

    /*
    @Override
    public Node getChild( String name ) throws NotFoundException {
        Node node = AbstractNode.getNodeByTitle( this, name, getType() );
        if( node != null ) {
            return node;
        } else {
            throw new NotFoundException( "The child " + name + " was not found" );
        }
    }
    */
}
