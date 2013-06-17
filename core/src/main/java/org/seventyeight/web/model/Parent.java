package org.seventyeight.web.model;

import java.util.List;

/**
 * @author cwolfgang
 */
public interface Parent {
    public Node getChild( String name ) throws NotFoundException;
    public List<Node> getChildren();
}
