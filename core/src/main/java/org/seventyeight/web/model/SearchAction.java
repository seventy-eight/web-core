package org.seventyeight.web.model;

/**
 * @author cwolfgang
 */
public abstract class SearchAction implements Action, Parent {

    protected Node parent;

    public SearchAction( Node parent ) {
        this.parent = parent;
    }
}
