package org.seventyeight.web.project.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.SavingException;
import org.seventyeight.web.nodes.Group;

/**
 * @author cwolfgang
 */
public class Role extends Group {

    public static final String ROLE = "role";
    public static final String ROLES = "roles";

    public Role( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public Saver getSaver( CoreRequest request ) {
        return new RoleSaver( this, request );
    }

    @Override
    protected String getGroupType() {
        return ROLES;
    }

    public class RoleSaver extends GroupSaver {
        public RoleSaver( AbstractNode modelObject, CoreRequest request ) {
            super( modelObject, request );
        }

        @Override
        public void save() throws SavingException {
            super.save();

            //set( "title", ROLES );
            //set( ROLE );
        }
    }

    public String getName() {
        return document.get( "title", "UNKNOWN" );
    }


    public static class RoleDescriptor extends GroupDescriptor {

        @Override
        public String getDisplayName() {
            return ROLES;
        }

        @Override
        public String getType() {
            return ROLE;
        }
    }
}
