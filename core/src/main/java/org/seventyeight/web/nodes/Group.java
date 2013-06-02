package org.seventyeight.web.nodes;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;
import org.seventyeight.web.model.*;

import java.util.List;

/**
 * @author cwolfgang
 */
public class Group extends Entity<Group> {

    private static Logger logger = Logger.getLogger( Group.class );

    public static final String GROUPS = "groups";
    public static final String GROUP = "group";

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
            /*
            String name = request.getValue( "title", null );
            if( name == null || name.isEmpty() ) {
                throw new SavingException( "The title must be set" );
            }
            document.set( "title", name );
            */
        }
    }

    public String getName() {
        return document.get( "name" );
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    /**
     * Add a {@link User} to this {@link Group}.
     */
    public void addMember( User user ) {
        user.addGroup( this );
        /*
        if( !isMember( user ) ) {
            logger.debug( "Adding " + user + " to " + this );
            updateField( GROUPS, new MongoUpdate().push( "members", user.getObjectId() ) );
            save();
        } else {
            logger.debug( user + " is already a member of " + this );
        }
        */
    }

    /**
     * Determine whether or not the {@link User} is member of this {@link Group}.
     */
    public boolean isMember( User user ) {
        List<Group> groups = user.getGroups();

        for( Group g : groups ) {
            if( this.equals( g ) ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public String getMainTemplate() {
        return "org/seventyeight/web/main.vm";
    }

    public static class GroupDescriptor extends NodeDescriptor<Group> {

        @Override
        public Node getChild( String name ) throws NotFoundException {
            return null;
        }

        @Override
        public String getDisplayName() {
            return "Group";
        }

        @Override
        public String getType() {
            return "group";
        }
    }
}
