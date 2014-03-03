package org.seventyeight.web.model.extensions;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.NotFoundException;

import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class NodeListener {
    private static Logger logger = LogManager.getLogger(NodeListener.class);

    public void onNodeCreated(AbstractNode<?> node) throws ItemInstantiationException, NotFoundException, Exception {
        // No op
    }

    public static void fireOnNodeCreated(AbstractNode<?> node) {
        for(NodeListener listener : getAll()) {
            try {
                listener.onNodeCreated( node );
            } catch( Exception e ) {
                logger.log( Level.ERROR, "Unable to fire on created for " + node, e );
            }
        }
    }

    public static List<NodeListener> getAll() {
        return Core.getInstance().getExtensions( NodeListener.class );
    }
}
