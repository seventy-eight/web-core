package org.seventyeight.web.project.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.SavingException;
import org.seventyeight.web.nodes.Group;
import org.seventyeight.web.nodes.User;

/**
 * @author cwolfgang
 */
public class Role extends Group {

    private static Logger logger = LogManager.getLogger( Role.class );

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

    public boolean hasRole( User user ) {
        MongoDocument doc = new MongoDocument().set( "skill", getIdentifier() );
        MongoDBQuery query = new MongoDBQuery().elemMatch( "roles", doc ).getId( user.getIdentifier() );
        MongoDocument d = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).findOne( query );

        logger.debug( "Found role: " + d );
        if( d == null || d.isNull() ) {
            return false;
        } else {
            return true;
        }
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
