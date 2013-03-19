package org.seventyeight.web.nodes;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 *         Date: 18-02-13
 *         Time: 22:28
 */
public class User extends Entity {

    private static Logger logger = Logger.getLogger( User.class );

    public static final String USERS = "users";

    public enum Visibility {
        VISIBLE,
        HIDDEN;
    }

    public User( Node parent, MongoDocument document ) {
        super( parent, document );
    }


    @Override
    public Saver getSaver( CoreRequest request ) {
        return new UserSaver( this, request );
    }

    public class UserSaver extends Saver {

        private String username;

        public UserSaver( PersistedObject modelObject, CoreRequest request ) {
            super( modelObject, request );
        }

        @Override
        public void save() throws SavingException {
            username = request.getValue( "username", null );
            if( username == null || username.isEmpty() ) {
                throw new SavingException( "The username must be set" );
            }
            document.set( "username", username );
        }

        @Override
        public Object getId() {
            return username;
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

    /*
    @Override
    public Object getDynamic( Node parent, String token ) {
        User user = null;
        try {
            user = Users.getUserByUsername( parent, token );
        } catch( ItemInstantiationException e ) {
            logger.debug( e );
        }

        if( user == null ) {
            return super.getDynamic( parent, token );
        } else {
            return user;
        }
    }
    */

    @Override
    public String toString() {
        return "User[" + getUsername() + "]" ;
    }

    public static class UserDescriptor extends NodeDescriptor<User> {

        @Override
        public String getDisplayName() {
            return "User";
        }
    }
}
