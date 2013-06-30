package org.seventyeight.web.actions;

import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class Login implements Node {

    protected Node parent;

    public Login( Node parent ) {
        this.parent = parent;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public String getDisplayName() {
        return "Login";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

}
