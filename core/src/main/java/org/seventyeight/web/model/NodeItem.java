package org.seventyeight.web.model;

/**
 * @author cwolfgang
 */
public interface NodeItem {

    public NodeItem getParent();
    public NodeItem getNode( String name ) throws NotFoundException;
    public String getDisplayName();
}
