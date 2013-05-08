package org.seventyeight.web.actions;

import org.seventyeight.web.Core;
import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;

/**
 * @author cwolfgang
 */
public class GlobalConfiguration implements Action {
    @Override
    public String getUrlName() {
        return "configuration";
    }

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public Node getChild( String name ) throws NotFoundException {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Global configuration";
    }

    @Override
    public String getMainTemplate() {
        return Core.MAIN_TEMPLATE;
    }
}
