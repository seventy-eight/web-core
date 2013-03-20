package org.seventyeight.web.model;

import org.apache.log4j.Logger;
import org.seventyeight.web.Core;

/**
 * @author cwolfgang
 */
public abstract class NodeDescriptor<T extends AbstractNode> extends Descriptor<T> implements Node {

    private static Logger logger = Logger.getLogger( NodeDescriptor.class );

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public T newInstance( String title ) throws ItemInstantiationException {
        logger.debug( "New instance for " + clazz );
        T node = Core.getInstance().createNode( clazz, getCollectionName() );

        node.getDocument().set( "type", getType() );
        node.getDocument().set( "title", title );

        return node;
    }

    public String getCollectionName() {
        return Core.NODE_COLLECTION_NAME;
    }

    public abstract String getType();
}
