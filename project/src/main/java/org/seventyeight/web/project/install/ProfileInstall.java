package org.seventyeight.web.project.install;

import org.seventyeight.database.DBInstallable;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.Core;
import org.seventyeight.web.CoreException;
import org.seventyeight.web.installers.NodeInstaller;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.project.model.Profile;
import org.seventyeight.web.utilities.Parameters;

/**
 * @author cwolfgang
 */
public class ProfileInstall extends NodeInstaller<User> {

    protected String firstName;
    protected String lastName;
    protected String email;

    protected Profile profile;

    public ProfileInstall( String title, String firstName, String lastName, String email ) {
        super( title );
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Override
    protected Descriptor<User> getDescriptor() {
        return Core.getInstance().getDescriptor( Profile.class );
    }

    @Override
    protected void setParameters( Parameters parameters ) {
        parameters.put( "username", title );
        parameters.put( "email", email );
        parameters.put( Profile.FIRST_NAME, firstName );
        parameters.put( Profile.LAST_NAME, lastName );
    }

    @Override
    protected Profile getNodeFromDB() {
        return Profile.getProfileByUsername( Core.getInstance(), title );
    }
}
