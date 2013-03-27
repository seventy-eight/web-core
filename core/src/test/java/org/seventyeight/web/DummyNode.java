package org.seventyeight.web;

import org.seventyeight.web.model.Node;

/**
 * @author cwolfgang
 *         Date: 03-03-13
 *         Time: 22:38
 */
public class DummyNode implements Node {

    protected Node parent;

    public DummyNode( Node parent ) {
        this.parent = parent;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public Node getChild( String name ) {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Dummy";
    }

    @Override
    public String getMainTemplate() {
        return "org/seventyeight/web/main.vm";
    }
}
