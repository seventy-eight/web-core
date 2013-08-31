package org.seventyeight.web.installers;

import org.seventyeight.web.Core;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.nodes.Group;
import org.seventyeight.web.nodes.User;

/**
 * @author cwolfgang
 */
public class GroupInstall extends DefaultNodeInstall<Group> {

    public GroupInstall( String title, User owner ) {
        super( title, owner );
        this.owner = owner;
    }

    @Override
    protected Descriptor<Group> getDescriptor() {
        return Core.getInstance().getDescriptor( Group.class );
    }
}
