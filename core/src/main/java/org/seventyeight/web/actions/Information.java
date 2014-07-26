package org.seventyeight.web.actions;

import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class Information implements Node {

    private Core core;

    public Information( Core core ) {
        this.core = core;
    }

    @Override
    public Node getParent() {
        return core.getRoot();
    }

    @Override
    public String getDisplayName() {
        return "Information";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

}
