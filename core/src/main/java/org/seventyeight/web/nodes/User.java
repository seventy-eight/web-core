package org.seventyeight.web.nodes;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;
import org.seventyeight.web.model.*;

import java.util.List;

/**
 * @author cwolfgang
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
        return getTitle();
    }

    @Override
    public String toString() {
        return "User[" + getUsername() + "]" ;
    }

    public static User getUserByUsername( Node parent, String username ) throws ItemInstantiationException {
        List<MongoDocument> docs = MongoDBCollection.get( User.USERS ).find( new MongoDBQuery().is( "username", username ), 0, 1 );

        if( docs != null && !docs.isEmpty() ) {
            return new User( parent, docs.get( 0 ) );
        } else {
            throw new ItemInstantiationException( "The user " + username + " not found" );
        }
    }

    public static class UserDescriptor extends NodeDescriptor<User> {

        @Override
        public String getDisplayName() {
            return "User";
        }

        @Override
        public String getType() {
            return "user";
        }

        @Override
        public Node getChild( String name ) throws NotFoundException {
            try {
                return getUserByUsername( this, name );
            } catch( ItemInstantiationException e ) {
                logger.debug( e );
            }

            throw new NotFoundException( "The user " + name + " was not found" );
        }
    }
}
