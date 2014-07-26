package org.seventyeight.web.model;

/**
 * @author cwolfgang
 */
public interface RootNode extends Node {
    public void addNode( String urlName, Node node );
    public Node getChild( String name );
}
