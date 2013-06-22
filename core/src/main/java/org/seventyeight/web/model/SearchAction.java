package org.seventyeight.web.model;

/**
 * @author cwolfgang
 */
public abstract class SearchAction implements Node {

    protected Node parent;

    public SearchAction( Node parent ) {
        this.parent = parent;
    }

    @Override
    public Node getParent() {
        return parent;
    }
}
