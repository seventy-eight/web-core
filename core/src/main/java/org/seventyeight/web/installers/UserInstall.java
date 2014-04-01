package org.seventyeight.web.installers;

import org.seventyeight.web.Core;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.nodes.Group;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.utilities.Parameters;

/**
 * @author cwolfgang
 */
public class UserInstall extends NodeInstaller<User> {

    protected String email;
    protected boolean visible = true;

    public UserInstall( String title, String email ) {
        super( title );

        this.email = email;
    }

    public UserInstall setVisibility(boolean visibility) {
        this.visible = visibility;
        return this;
    }

    @Override
    protected Descriptor<User> getDescriptor() {
        return Core.getInstance().getDescriptor( User.class );
    }

    @Override
    protected void setParameters( Parameters parameters ) {
        parameters.put( "title", title );
        parameters.put( "username", title );
        parameters.put( "email", email );
        parameters.put( "password", "pass" );
        parameters.put( "password_again", "pass" );
    }

    @Override
    protected User getNodeFromDB() {
        return User.getUserByUsername( Core.getInstance(), title );
    }
}
