package org.seventyeight.web.project.install;

import org.apache.log4j.Logger;
import org.seventyeight.database.DBInstallable;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.Core;
import org.seventyeight.web.CoreException;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.project.model.Certificate;
import org.seventyeight.web.project.model.Profile;
import org.seventyeight.web.project.model.Role;
import org.seventyeight.web.utilities.Parameters;

/**
 * @author cwolfgang
 */
public class AdminRoleInstall implements DBInstallable<Role> {

    private static Logger logger = Logger.getLogger( AdminRoleInstall.class );

    public static final String ADMIN_NAME_PL = "Administrators";
    public static final String ADMIN_NAME = "Administrator";

    protected Role role;

    private Profile owner;

    public AdminRoleInstall( Profile owner ) {
        this.owner = owner;
    }

    @Override
    public Role getValue() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void install() throws DatabaseException {
        try {
            Role role = (Role) Core.getInstance().getDescriptor( Role.class ).newInstance( ADMIN_NAME );

            Parameters p = new Parameters();

            p.put( "title", ADMIN_NAME_PL );
            p.put( "role", ADMIN_NAME );
            p.setUser( owner );


            role.save( p, null );
            this.role = role;
        } catch( CoreException e ) {
            throw new DatabaseException( "Unable to create admin role, " + e.getMessage(), e );
        } catch( ClassNotFoundException e ) {
            throw new DatabaseException( e );
        }
    }

    public Role getRole() {
        return role;
    }

    @Override
    public boolean isInstalled() throws DatabaseException {
        Role role = (Role) AbstractNode.getNodeByTitle( Core.getInstance(), ADMIN_NAME_PL );
        if( role != null ) {
            this.role = role;
            return true;
        } else {
            return false;
        }
    }
}
