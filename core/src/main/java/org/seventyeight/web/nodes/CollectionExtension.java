package org.seventyeight.web.nodes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.model.Layoutable;
import org.seventyeight.web.model.Node;

/**
 * @author cwolfgang
 */
public class CollectionExtension implements Layoutable {

    private static Logger logger = LogManager.getLogger( CollectionExtension.class );

    @Override
    public boolean isApplicable( Node node ) {
        logger.debug( "NODE: {}, parent: {}", node, node.getParent() );
        if(node.getParent() != null && node.getParent() instanceof Collection) {
            return true;
        } else {
            return false;
        }
    }

    public String getNextUrl(Node node) {
        Collection c = (Collection) node.getParent();
        return c.getUrl() + "get/" + (c.getIndex() + 1);
    }

    public String getPreviousUrl(Node node) {
        Collection c = (Collection) node.getParent();
        return c.getUrl() + "get/" + (c.getIndex() - 1);
    }

    public boolean hasNext(Node node) {
        Collection c = (Collection) node.getParent();
        return (c.getIndex() + 1) < c.size();
    }

    public boolean hasPrevious(Node node) {
        Collection c = (Collection) node.getParent();
        return (c.getIndex() - 1) > -1;
    }
}
