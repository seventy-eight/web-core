package org.seventyeight.web.nodes;

import org.apache.log4j.Logger;
import org.apache.log4j.lf5.LogLevel;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;
import org.seventyeight.utils.Utils;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class User extends Entity<User> {

    private static Logger logger = Logger.getLogger( User.class );

    public static final String USERS = "users";

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String USERNAME = "username";

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
        MongoDocument doc = MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).findOne( new MongoDBQuery().is( "username", username ) );

        if( doc != null && !doc.isNull() ) {
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

            /* Password */
            String password1 = request.getValue( "password", null );
            String password2 = request.getValue( "password_again", null );
            if( ( password1 == null || password1.isEmpty() ) || ( password2 == null || password2.isEmpty() ) ) {
                throw new SavingException( "The password cannot be empty" );
            }

            if( !password1.equals( password2 ) ) {
                throw new SavingException( "Passwords does not match" );
            }
            String hashed = "";
            try {
                hashed = Utils.md5( password1 );
            } catch( NoSuchAlgorithmException e ) {
                throw new SavingException( "Unable to hash password" );
            }
            document.set( "password", hashed );
        }
    }

    public List<Group> getGroups() {
        logger.debug( "Listing groups for " + this );

        List<MongoDocument> docs = document.getMappedList( Group.GROUPS, Core.NODE_COLLECTION_NAME );

        List<Group> groups = new ArrayList<Group>( docs.size() );

        for( MongoDocument d : docs ) {
            groups.add( new Group( this, d ) );
        }

        return groups;
    }

    @Override
    public void setOwner( User owner ) {
        /* No op for users */
    }

    public void setUsername( String username ) {
        MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).update( new MongoDBQuery().is( "_id", getObjectId() ), new MongoUpdate().set( "username", username ) );
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
            return "/theme/unknown-person.png";
        } else {
            return "/theme/unknown-person.png";
        }
    }

    /**
     * Get a {@link User} by Username. Returns null if not found.
     */
    public static User getUserByUsername( Node parent, String username ) {
        MongoDocument docs = MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).findOne( new MongoDBQuery().is( "username", username ) );

        if( docs != null ) {
            try {
                return Core.getInstance().getItem( parent, docs );
            } catch( ItemInstantiationException e ) {
                logger.error( e );
                return null;
            }
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

        /*
        @Override
        public String getCollectionName() {
            return USERS;
        }
        */

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
            //u.getDocument().set( "_id", title );

            return u;
        }

        @Override
        public void save( Request request, Response response ) {
            logger.debug( "Saving " + this );

            testString = "The millis: " + System.currentTimeMillis();
        }

        @Override
        public boolean allowIdenticalNaming() {
            return false;
        }
    }
}
