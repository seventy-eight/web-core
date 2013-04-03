package org.seventyeight.web.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;

import java.lang.reflect.Constructor;

/**
 * @author cwolfgang
 */
public abstract class NodeDescriptor<T extends Describable<T>> extends Descriptor<T> implements Node {

    private static Logger logger = Logger.getLogger( NodeDescriptor.class );

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public T newInstance( String title ) throws ItemInstantiationException {
        logger.debug( "New instance for " + clazz );
        T node = createNode();

        node.getDocument().set( "type", getType() );
        node.getDocument().set( "title", title );

        return node;
    }

    protected T createNode( ) throws ItemInstantiationException {
        logger.debug( "Creating " + clazz.getName() );

        MongoDBCollection collection = MongoDBCollection.get( getCollectionName() );
        MongoDocument document = new MongoDocument();

        T instance = null;
        try {
            Constructor<T> c = clazz.getConstructor( Node.class, MongoDocument.class );
            instance = c.newInstance( this, document );
        } catch( Exception e ) {
            throw new ItemInstantiationException( "Unable to instantiate " + clazz.getName(), e );
        }

        document.set( "class", clazz.getName() );
        collection.save( document );

        return instance;
    }

    public String getCollectionName() {
        return Core.NODE_COLLECTION_NAME;
    }

    public abstract String getType();

    @Override
    public String getMainTemplate() {
        return "org/seventyeight/web/main.vm";
    }
}
