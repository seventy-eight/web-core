package org.seventyeight.web;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 *         Date: 18-02-13
 *         Time: 22:45
 */
public class Group extends Entity {

    public static final String GROUPS = "groups";

    public Group( MongoDocument document ) {
        super( document );
    }

    @Override
    public Saver getSaver( CoreRequest request ) {
        return new GroupSaver( this, request );
    }

    public class GroupSaver extends Saver {

        public GroupSaver( AbstractItem item, CoreRequest request ) {
            super( item, request );
        }

        @Override
        public void save() throws SavingException {
            String name = request.getValue( "name", null );
            if( name == null || name.isEmpty() ) {
                throw new SavingException( "The name must be set" );
            }
            document.set( "name", name );
        }
    }

    public String getName() {
        return document.get( "name" );
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    public void addMember( User user ) {
        updateField( GROUPS, new MongoUpdate().push( "members", user.getObjectId() ) );
    }

    public static class GroupDescriptor extends Descriptor<Group> {

        @Override
        public String getCollectionName() {
            return GROUPS;
        }

        @Override
        public String getDisplayName() {
            return "Group";
        }
    }
}
