package org.seventyeight.web.nodes;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class Group extends Entity<Group> {

    public static final String GROUPS = "groups";

    public Group( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String getPortrait() {
        return null;
    }

    @Override
    public Saver getSaver( CoreRequest request ) {
        return new GroupSaver( this, request );
    }

    public class GroupSaver extends Saver {

        public GroupSaver( AbstractNode modelObject, CoreRequest request ) {
            super( modelObject, request );
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

    @Override
    public String getMainTemplate() {
        return "org/seventyeight/web/main.vm";
    }

    public static class GroupDescriptor extends Descriptor<Group> {

        @Override
        public String getDisplayName() {
            return "Group";
        }
    }
}
