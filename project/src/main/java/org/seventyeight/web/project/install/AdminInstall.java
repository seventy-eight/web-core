package org.seventyeight.web.project.install;

import org.apache.log4j.Logger;
import org.seventyeight.database.DBInstallable;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.Core;
import org.seventyeight.web.CoreException;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.project.model.Profile;
import org.seventyeight.web.utilities.Parameters;

/**
 * @author cwolfgang
 */
public class AdminInstall implements DBInstallable {

    private static Logger logger = Logger.getLogger( AdminInstall.class );

    public static final String ADMIN_NAME = "admin";

    @Override
    public void install() throws DatabaseException {
        try {
            Profile profile = (Profile) Core.getInstance().getDescriptor( Profile.class ).newInstance( ADMIN_NAME );

            Parameters p = new Parameters();

            p.put( "username", ADMIN_NAME );
            p.put( "email", "admin@mysite.dk" );
            p.put( Profile.FIRST_NAME, "Christian" );
            p.put( Profile.LAST_NAME, "Wolfgang" );

            profile.setVisible( true );

            profile.save( p, null );
        } catch( CoreException e ) {
            throw new DatabaseException( "Unable to create admin, " + e.getMessage(), e );
        } catch( ClassNotFoundException e ) {
            throw new DatabaseException( e );
        }
    }

    @Override
    public boolean isInstalled() throws DatabaseException {
        User user = User.getUserByUsername( Core.getInstance(), ADMIN_NAME );
        if( user != null ) {
            return true;
        } else {
            return false;
        }
    }
}
