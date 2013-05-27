package org.seventyeight.web.installers;

import org.seventyeight.database.DBInstallable;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.Core;
import org.seventyeight.web.CoreException;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.nodes.Group;
import org.seventyeight.web.utilities.Parameters;

/**
 * @author cwolfgang
 */
public class GroupInstall implements DBInstallable<Group> {

    protected String name;

    protected Group group;

    public GroupInstall( String name ) {
        this.name = name;
    }

    @Override
    public void install() throws DatabaseException {
        try {
            Group group = (Group) Core.getInstance().getDescriptor( Group.class ).newInstance( name );

            Parameters p = new Parameters();

            group.save( p, null );
            this.group = group;
        } catch( CoreException e ) {
            throw new DatabaseException( "Unable to create wolfgang, " + e.getMessage(), e );
        } catch( ClassNotFoundException e ) {
            throw new DatabaseException( e );
        }
    }

    @Override
    public Group getValue() {
        return group;
    }

    @Override
    public boolean isInstalled() throws DatabaseException {
        Group group = (Group) AbstractNode.getNodeByTitle( Core.getInstance(), name );
        if( this.group != null ) {
            this.group = group;
            return true;
        } else {
            return false;
        }
    }
}
