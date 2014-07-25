package org.seventyeight.web;

import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class Root implements TopLevelNode, RootNode, Parent {

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
        return null;
    }

    @Override
    public Node getChild( String name ) throws NotFoundException {
        if( children.containsKey( name ) ) {
            return children.get( name );
        } else {
            return null;
        }
    }
}
