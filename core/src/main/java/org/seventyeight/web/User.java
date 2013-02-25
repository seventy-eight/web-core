package org.seventyeight.web;

import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;
import org.seventyeight.web.model.*;

import java.util.List;

/**
 * @author cwolfgang
 *         Date: 18-02-13
 *         Time: 22:28
 */
public class User extends Entity {

    public static final String USERS = "users";

    public enum Visibility {
        VISIBLE,
        HIDDEN;
    }

    public User( MongoDocument document ) {
        super( document );
    }

    @Override
    public Saver getSaver( CoreRequest request ) {
        return new UserSaver( this, request );
    }

    public class UserSaver extends Saver {

        public UserSaver( AbstractModelObject modelObject, CoreRequest request ) {
            super( modelObject, request );
        }

        @Override
        public void save() throws SavingException {
            String username = request.getValue( "username", null );
            if( username == null || username.isEmpty() ) {
                throw new SavingException( "The username must be set" );
            }
            document.set( "username", username );
        }
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

    @Override
    public String getDisplayName() {
        return getUsername();
    }

    public static User getUserByUsername( String username ) throws ItemInstantiationException {
        List<MongoDocument> docs = MongoDBCollection.get( USERS ).find( new MongoDBQuery().is( "username", username ), 0, 1 );

        if( docs != null && !docs.isEmpty() ) {
            return new User( docs.get( 0 ) );
        } else {
            throw new ItemInstantiationException( "The user " + username + " not found" );
        }
    }

    @Override
    public String toString() {
        return "User[" + getUsername() + "]" ;
    }

    public static class UserDescriptor extends Descriptor<User> {

        @Override
        public String getCollectionName() {
            return USERS;
        }

        @Override
        public String getDisplayName() {
            return "User";
        }
    }
}
