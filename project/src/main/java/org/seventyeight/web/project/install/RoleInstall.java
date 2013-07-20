package org.seventyeight.web.project.install;

import org.seventyeight.web.Core;
import org.seventyeight.web.installers.DefaultNodeInstall;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.nodes.Group;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.project.model.Certificate;
import org.seventyeight.web.project.model.Role;

/**
 * @author cwolfgang
 */
public class RoleInstall extends DefaultNodeInstall<Group> {

    public RoleInstall( String title, User owner ) {
        super( title, owner );
    }

    @Override
    protected Descriptor<Group> getDescriptor() {
        return Core.getInstance().getDescriptor( Role.class );
    }
}
