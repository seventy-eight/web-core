package org.seventyeight.web.installers;

import com.google.gson.JsonObject;
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

    public UserInstall( Core core, String title, String email ) {
        super( core, title );

        this.email = email;
    }

    public UserInstall setVisibility(boolean visibility) {
        this.visible = visibility;
        return this;
    }

    @Override
    protected Descriptor<User> getDescriptor() {
        return core.getDescriptor( User.class );
    }

    @Override
    protected void setJson( JsonObject json ) {
        json.addProperty( "title", title );
        json.addProperty( "username", title );
        json.addProperty( "email", email );
        json.addProperty( "password", "pass" );
        json.addProperty( "password_again", "pass" );
    }

    @Override
    protected User getNodeFromDB() {
        return User.getUserByUsername( core.getRoot(), title );
    }
}
