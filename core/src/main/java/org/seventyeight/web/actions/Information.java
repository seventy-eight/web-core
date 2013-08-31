package org.seventyeight.web.actions;

import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class Information implements Node {

    @Override
    public Node getParent() {
        return Core.getInstance();
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
