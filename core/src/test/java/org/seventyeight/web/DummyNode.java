package org.seventyeight.web;

import org.seventyeight.web.model.NodeItem;

/**
 * @author cwolfgang
 *         Date: 03-03-13
 *         Time: 22:38
 */
public class DummyNode implements NodeItem {

    protected NodeItem parent;

    public DummyNode( NodeItem parent ) {
        this.parent = parent;
    }

    @Override
    public NodeItem getParent() {
        return parent;
    }

    @Override
    public NodeItem getNode( String name ) {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Dummy";
    }
}
