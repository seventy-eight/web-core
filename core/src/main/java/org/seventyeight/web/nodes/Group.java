package org.seventyeight.web.nodes;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Group extends Resource<Group> {

    private static Logger logger = Logger.getLogger( Group.class );

    public static final String GROUPS = "groups";
    public static final String GROUP = "group";

    public Group( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String getPortrait() {
        return "/theme/framed-group-small.png";
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

    protected String getGroupType() {
        return GROUPS;
    }

    /**
     * Add a {@link User} to this {@link Group}.
     */
    public void addMember( User user ) {
        if( !isMember( user ) ) {
            logger.debug( "Adding " + user + " to " + this );
            //document.addToList( getGroupType(), user.getIdentifier() );
            user.getDocument().addToList( getGroupType(), getIdentifier() );
            user.save();
        } else {
            logger.debug( user + " is already a member of " + this );
        }
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

    public List<User> getMembers() {
        logger.debug( "Getting members for " + this );

        MongoDBQuery q = new MongoDBQuery().is( GROUPS, getIdentifier() );
        List<MongoDocument> docs = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).find( q );

        logger.debug( "DOCS ARE: " + docs );

        List<User> users = new ArrayList<User>( docs.size() );

        for( MongoDocument d : docs ) {
            users.add( new User( this, d ) );
        }

        return users;
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
            return GROUP;
        }

        @Override
        public String getType() {
            return GROUPS;
        }
    }
}
