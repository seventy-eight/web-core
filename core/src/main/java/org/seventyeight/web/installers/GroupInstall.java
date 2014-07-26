package org.seventyeight.web.installers;

import com.google.gson.JsonObject;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.nodes.Group;
import org.seventyeight.web.nodes.User;

/**
 * @author cwolfgang
 */
public class GroupInstall extends DefaultNodeInstall<Group> {

    public GroupInstall( Core core, String title, User owner ) {
        super( core, title, owner );
        this.owner = owner;
    }

    @Override
    protected void setJson( JsonObject json ) {
      /* Implementation is a no op */
    }

    @Override
    protected Descriptor<Group> getDescriptor() {
        return core.getDescriptor( Group.class );
    }
}
