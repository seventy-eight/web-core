package org.seventyeight.web.nodes;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;
import org.seventyeight.utils.Utils;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.UserPortrait;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.utilities.ExtensionUtils;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class User extends Resource<User> {

    private static Logger logger = LogManager.getLogger( User.class );

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
        MongoDocument doc = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).findOne( new MongoDBQuery().is( "username", username ) );

        if( doc != null && !doc.isNull() ) {
            return doc;
        } else {
            logger.debug( "The username " + username + " was not found" );
            return null;
        }
    }

    @Override
    public void updateNode(CoreRequest request, JsonObject jsonData) {

        /* Set username */
        String username = request.getValue( "username", null );
        if( username == null || username.isEmpty() ) {
            throw new IllegalArgumentException( "The username must be set" );
        }

        /* Set email */
        String email = request.getValue( "email", null );
        if( email == null || email.isEmpty() ) {
            throw new IllegalArgumentException( "The email must be set" );
        }

        /* Password */
        String password1 = request.getValue( "password", null );
        String password2 = request.getValue( "password_again", null );
        if( ( password1 == null || password1.isEmpty() ) || ( password2 == null || password2.isEmpty() ) ) {
            throw new IllegalArgumentException( "The password cannot be empty" );
        }

        if( !password1.equals( password2 ) ) {
            throw new IllegalArgumentException( "Passwords does not match" );
        }
        String hashed = "";
        try {
            hashed = Utils.md5( password1 );
        } catch( NoSuchAlgorithmException e ) {
            throw new IllegalArgumentException( "Unable to hash password" );
        }

        document.set( "username", username );
        document.set( "email", email );
        document.set( "password", hashed );
    }

    public List<Group> getGroups() {
        logger.debug( "Listing groups for " + this );

        List<MongoDocument> docs = document.getMappedList( Group.GROUPS, Core.NODES_COLLECTION_NAME );

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
        MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).update( new MongoDBQuery().is( "_id", getObjectId() ), new MongoUpdate().set( "username", username ) );
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
        return document.get( EMAIL, "" );
    }

    @Override
    protected void setMandatoryFields( User owner ) {
        super.setMandatoryFields( owner );

        document.set( "owner", owner.getIdentifier() );
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
    public void setPortrait( Request request, JsonObject json ) {
        try {
            UserPortrait.UserPortraitDescriptor descriptor = (UserPortrait.UserPortraitDescriptor) Core.getInstance().getDescriptor( json.get( "class" ).getAsString() );
            UserPortrait userPortrait = descriptor.newInstance(request, this, "portrait");
            userPortrait.update( request );
            //ExtensionUtils.retrieveExtensions( request, json, userPortrait );
            //userPortrait.save( request, json );
            document.set( "portrait", userPortrait.getDocument() );
            save();
        } catch( Exception e ) {
            logger.warn( "failed", e );
        }
    }

    @Override
    public String getMainTemplate() {
        return "org/seventyeight/web/main.vm";
    }

    @Override
    public String getPortrait() {
        logger.debug( "Getting portrait for {}", this );

        MongoDocument portrait = document.getSubDocument( "portrait", null );

        if( portrait != null && !portrait.isNull() ) {
            try {
                UserPortrait up = Core.getInstance().getItem( this, portrait );
                return up.getUrl();
            } catch( ItemInstantiationException e ) {
                logger.warn( "Unable to get the portrait from " + portrait );
                return "/theme/unknown-person.png";
            }
        } else {
            return "/theme/unknown-person.png";
        }
    }

    /**
     * Get a list of the registered portrait descriptors
     * @return
     */
    public List<? extends UserPortrait.UserPortraitDescriptor> getUserPortraitDescriptors() {
        List<UserPortrait.UserPortraitDescriptor> descriptors = Core.getInstance().getExtensionDescriptors( UserPortrait.class );

        return descriptors;
    }

    public UserPortrait getPortraitExtension() throws ItemInstantiationException {
        MongoDocument portrait = document.getSubDocument( "portrait", null );

        if( portrait != null ) {
            UserPortrait up = Core.getInstance().getItem( this, portrait );
            return up;
        } else {
            return null;
        }
    }

    /**
     * Get a {@link User} by Username. Returns null if not found.
     */
    public static User getUserByUsername( Node parent, String username ) {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        MongoDocument userDoc = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).findOne( new MongoDBQuery().is( "username", username ) );

        logger.debug( "USER DOOOOOOOOOOOOC: {}", userDoc );
        if( userDoc != null && !userDoc.isNull() ) {
            try {
                return Core.getInstance().getItem( parent, userDoc );
            } catch( ItemInstantiationException e ) {
                logger.error( e );
                return null;
            }
        } else {
            logger.debug( "The user " + username + " was not found" );
            return null;
        }
    }

    @Override
    public User getOwner() throws ItemInstantiationException {
        logger.debug( "Return myself, {}", this );
        return this;
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
        public User newInstance(CoreRequest request) throws ItemInstantiationException {
            User u = super.newInstance(request);
            u.getDocument().set( "username", title );
            //u.getDocument().set( "_id", title );

            return u;
        }
        */

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
