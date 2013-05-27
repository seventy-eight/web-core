package org.seventyeight.web.nodes;

import org.apache.log4j.Logger;
import org.seventyeight.database.annotations.Persisted;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.util.List;

/**
 * @author cwolfgang
 */
public class User extends Entity<User> {

    private static Logger logger = Logger.getLogger( User.class );

    public static final String USERS = "users";

    public enum Visibility {
        VISIBLE,
        HIDDEN;
    }

    public User( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    /**
     * Return the document corresponding to the given username,
     */
    public static MongoDocument getUserDocumentByUsername( String username ) {
        MongoDocument doc = MongoDBCollection.get( User.USERS ).findOne( new MongoDBQuery().is( "username", username ) );

        if( doc != null ) {
            return doc;
        } else {
            logger.debug( "The username " + username + " was not found" );
            return null;
        }
    }

    @Override
    public Saver getSaver( CoreRequest request ) {
        return new UserSaver( this, request );
    }

    public class UserSaver extends Saver {

        protected String username;
        protected String email;

        public UserSaver( AbstractNode modelObject, CoreRequest request ) {
            super( modelObject, request );
        }

        @Override
        public void save() throws SavingException {

            /* Set username */
            username = request.getValue( "username", null );
            if( username == null || username.isEmpty() ) {
                throw new SavingException( "The username must be set" );
            }
            document.set( "username", username );

            /* Set email */
            email = request.getValue( "email", null );
            if( email == null || email.isEmpty() ) {
                throw new SavingException( "The email must be set" );
            }
            document.set( "email", email );
        }

        @Override
        public Object getId() {
            return username;
        }
    }

    public void addGroup( Group group ) {
        MongoDocument d = new MongoDocument().set( Group.GROUP, group.getIdentifier() );
        document.addToList( Group.GROUPS, d );
        this.save();
    }

    @Override
    public void setOwner( User owner ) {
        /* No op for users */
    }

    public void setUsername( String username ) {
        MongoDBCollection.get( USERS ).update( new MongoUpdate().set( "username", username ), new MongoDBQuery().is( "_id", getObjectId() ) );
    }

    public String getUsername() {
        return document.get( "username" );
    }

    public String getPassword() {
        return document.get( "password" );
    }

    public void setVisibility( Visibility visibility ) {
        document.set( "visible", visibility.equals( Visibility.VISIBLE ) );
    }

    public void setVisible( boolean visible ) {
        document.set( "visible", visible );
    }

    public boolean isVisible() {
        return document.get( "visible", true );
    }

    public String getEmail() {
        return document.get( "email", null );
    }

    @Override
    public String getDisplayName() {
        return getTitle();
    }

    @Override
    public String toString() {
        return "User[" + getUsername() + "]" ;
    }

    @Override
    public String getMainTemplate() {
        return "org/seventyeight/web/main.vm";
    }

    @Override
    public String getPortrait() {
        if( false ) {
            return "/theme/default/unknown-person.png";
        } else {
            return "/theme/default/unknown-person.png";
        }
    }

    /**
     * Get a {@link User} by Username. Returns null if not found.
     */
    public static User getUserByUsername( Node parent, String username ) {
        List<MongoDocument> docs = MongoDBCollection.get( User.USERS ).find( new MongoDBQuery().is( "username", username ), 0, 1 );

        if( docs != null && !docs.isEmpty() ) {
            return new User( parent, docs.get( 0 ) );
        } else {
            logger.debug( "The user " + username + " was not found" );
            return null;
        }
    }

    public static class UserDescriptor extends NodeDescriptor<User> {

        public String testString;

        @Override
        public String getDisplayName() {
            return "User";
        }

        @Override
        public String getType() {
            return "user";
        }

        @Override
        public String getCollectionName() {
            return USERS;
        }

        @Override
        public Node getChild( String name ) throws NotFoundException {
            User user = getUserByUsername( this, name );
            if( user != null ) {
                return user;
            } else {
                throw new NotFoundException( "The user " + name + " was not found" );
            }
        }

        @Override
        public User newInstance( String title ) throws ItemInstantiationException {
            User u = super.newInstance( title );
            u.getDocument().set( "username", title );
            u.getDocument().set( "_id", title );

            return u;
        }

        @Override
        public void save( Request request, Response response ) {
            logger.debug( "Saving " + this );

            testString = "The millis: " + System.currentTimeMillis();
        }
    }
}
