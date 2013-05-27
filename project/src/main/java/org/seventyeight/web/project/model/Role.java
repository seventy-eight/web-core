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

    public static final String ROLE_STRING_TITLE = "role";
    public static final String ROLE_STRING_TITLE_PL = "roles";

    public Role( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public Saver getSaver( CoreRequest request ) {
        return new RoleSaver( this, request );
    }

    public class RoleSaver extends GroupSaver {
        public RoleSaver( AbstractNode modelObject, CoreRequest request ) {
            super( modelObject, request );
        }

        @Override
        public void save() throws SavingException {
            super.save();

            set( "title", ROLE_STRING_TITLE_PL );
            set( ROLE_STRING_TITLE );
        }
    }

    public String getName() {
        return document.get( "name", "UNKNOWN" );
    }


    public static class RoleDescriptor extends GroupDescriptor {
        @Override
        public String getDisplayName() {
            return "Roles";
        }
    }
}
