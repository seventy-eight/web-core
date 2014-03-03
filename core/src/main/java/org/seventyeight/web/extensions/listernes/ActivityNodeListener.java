package org.seventyeight.web.extensions.listernes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.model.*;
import org.seventyeight.web.model.extensions.NodeListener;

/**
 * @author cwolfgang
 */
public class ActivityNodeListener extends NodeListener {
    private static Logger logger = LogManager.getLogger(ActivityNodeListener.class);
    @Override
    public void onNodeCreated( AbstractNode<?> node ) throws Exception {
        logger.debug( "On created {}", node );
        if(node instanceof Resource) {
            //Activity activity = Activity.create( node.getOwner(), Activity.DefaultTypes.CREATED, (Resource<?>) node );
        }
    }
}
