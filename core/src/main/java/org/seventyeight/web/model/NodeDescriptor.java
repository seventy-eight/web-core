package org.seventyeight.web.model;

import org.apache.log4j.Logger;
import org.seventyeight.web.Core;

/**
 * @author cwolfgang
 *         Date: 06-03-13
 *         Time: 08:55
 */
public abstract class NodeDescriptor<T extends AbstractNodeItem> extends Descriptor<T> {

    private static Logger logger = Logger.getLogger( NodeDescriptor.class );

    @Override
    public T newInstance() throws ItemInstantiationException {
        logger.debug( "New instance for " + clazz );
        return Core.getInstance().createNode( clazz, getCollectionName() );
    }
}
