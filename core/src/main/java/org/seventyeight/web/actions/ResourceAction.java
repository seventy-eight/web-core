package org.seventyeight.web.actions;

import org.seventyeight.web.Core;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.model.Parent;

/**
 * @author cwolfgang
 */
public class ResourceAction implements Node, Parent {

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public String getDisplayName() {
        return "Resource";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    @Override
    public Node getChild( String name ) throws NotFoundException {
        try {
            return Core.getInstance().getNodeById( this, name );
        } catch( ItemInstantiationException e ) {
            throw new NotFoundException( e.getMessage(), "Unable to find " + name, e );
        }
    }
}
