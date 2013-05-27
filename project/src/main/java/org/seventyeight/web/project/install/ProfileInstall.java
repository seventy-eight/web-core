package org.seventyeight.web.project.install;

import org.seventyeight.database.DBInstallable;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.Core;
import org.seventyeight.web.CoreException;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.project.model.Profile;
import org.seventyeight.web.utilities.Parameters;

/**
 * @author cwolfgang
 */
public class ProfileInstall implements DBInstallable<Profile> {

    protected String name;
    protected String firstName;
    protected String lastName;
    protected String email;

    protected Profile profile;

    public ProfileInstall( String name, String firstName, String lastName, String email ) {
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Override
    public void install() throws DatabaseException {
        try {
            Profile profile = (Profile) Core.getInstance().getDescriptor( Profile.class ).newInstance( name );

            Parameters p = new Parameters();

            p.put( "username", name );
            p.put( "email", email );
            p.put( Profile.FIRST_NAME, firstName );
            p.put( Profile.LAST_NAME, lastName );

            profile.setVisible( true );

            profile.save( p, null );
            this.profile = profile;
        } catch( CoreException e ) {
            throw new DatabaseException( "Unable to create wolfgang, " + e.getMessage(), e );
        } catch( ClassNotFoundException e ) {
            throw new DatabaseException( e );
        }
    }

    @Override
    public Profile getValue() {
        return profile;
    }

    @Override
    public boolean isInstalled() throws DatabaseException {
        Profile profile = Profile.getProfileByUsername( Core.getInstance(), name );
        if( profile != null ) {
            this.profile = profile;
            return true;
        } else {
            return false;
        }
    }
}
