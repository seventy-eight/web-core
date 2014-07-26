package org.seventyeight.web.installers;

import org.seventyeight.web.Core;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.nodes.Group;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.utilities.Parameters;

/**
 * @author cwolfgang
 */
public abstract class DefaultNodeInstall<T extends AbstractNode<T>> extends NodeInstaller<T> {

    protected User owner;

    public DefaultNodeInstall( Core core, String title, User owner ) {
        super( core, title );
        this.owner = owner;
    }


    @Override
    public void after() {
        node.setOwner( owner );
    }

    @Override
    protected T getNodeFromDB() {
        return (T) AbstractNode.getNodeByTitle( core, core.getRoot(), title );
    }
}
