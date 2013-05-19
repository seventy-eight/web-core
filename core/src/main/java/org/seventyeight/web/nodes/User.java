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
public class User extends BaseUser<User> {

    private static Logger logger = Logger.getLogger( User.class );

    public User( Node parent, MongoDocument document ) {
        super( parent, document );
    }


    @Override
    public Saver getSaver( CoreRequest request ) {
        return new UserSaver( this, request );
    }

    public class UserSaver extends Saver {

        private String username;

        public UserSaver( AbstractNode modelObject, CoreRequest request ) {
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

    @Override
    public String toString() {
        return "User[" + getUsername() + "]" ;
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

    public static class UserDescriptor extends BaseUserDescriptor<User> {

        @Override
        public User newInstance( String title ) throws ItemInstantiationException {
            User u = super.newInstance( title );
            u.getDocument().set( "username", title );
            u.getDocument().set( "_id", title );

            return u;
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
        public void save( Request request, Response response ) {
            logger.debug( "Saving " + this );

            testString = "The millis: " + System.currentTimeMillis();
        }
    }
}
