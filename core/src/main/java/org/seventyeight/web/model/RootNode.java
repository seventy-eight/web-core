package org.seventyeight.web.model;

import org.seventyeight.web.Core;

/**
 * @author cwolfgang
 */
public interface RootNode extends Node, DeletingParent {
    public void addNode( String urlName, Node node );
    public Node getChild( String name );
    public void initialize(Core core);
}
