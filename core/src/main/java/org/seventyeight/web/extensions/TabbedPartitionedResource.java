package org.seventyeight.web.extensions;

import org.seventyeight.web.model.Layoutable;
import org.seventyeight.web.model.Node;

/**
 * @author cwolfgang
 */
public class TabbedPartitionedResource implements Layoutable {

    @Override
    public boolean isApplicable( Node node ) {
        return node instanceof Partitioned;
    }
}
