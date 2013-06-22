package org.seventyeight.web.project.actions;

import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.Node;

/**
 * @author cwolfgang
 */
public abstract class AddAction implements Node {

    protected Node parent;

    public AddAction( Node parent ) {
        this.parent = parent;
    }

    @Override
    public Node getParent() {
        return parent;
    }
}
