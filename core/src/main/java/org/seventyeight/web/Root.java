package org.seventyeight.web;

import org.seventyeight.web.actions.Get;
import org.seventyeight.web.actions.ResourceAction;
import org.seventyeight.web.handlers.template.TemplateManager;
import org.seventyeight.web.model.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author cwolfgang
 */
public class Root implements TopLevelNode, RootNode, Parent {

    /**
     * The Map of top level {@link Node}s
     */
    protected ConcurrentMap<String, Node> children = new ConcurrentHashMap<String, Node>();

    protected Core core;

    public Root(Core core) {
        /* Mandatory */
        children.put( "get", new Get( core, this ) );  // This
        children.put( "resource", new ResourceAction( core ) ); // Or that?
    }

    @Override
    public void save() {
      /* Implementation is a no op */
    }

    @Override
    public String getIdentifier() {
        return "root";
    }

    @Override
    public Node getParent() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "root";
    }

    @Override
    public String getMainTemplate() {
        return TemplateManager.getUrlFromClass( Root.class, "main.vm" );
    }

    @Override
    public Node getChild( String name ) {
        if( children.containsKey( name ) ) {
            return children.get( name );
        } else {
            return null;
        }
    }

    public void addNode( String urlName, Node node ) {
        children.put( urlName, node );
    }
}
