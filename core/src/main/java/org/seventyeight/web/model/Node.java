package org.seventyeight.web.model;

/**
 * @author cwolfgang
 */
public interface Node {
    public Node getParent();
    public Node getChild( String name ) throws NotFoundException;
    public String getDisplayName();

    public String getMainTemplate();
}
