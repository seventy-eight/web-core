package org.seventyeight.web.nodes;

import org.apache.log4j.Logger;
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
public abstract class BaseUser<T extends Entity> extends Entity<T> {

    private static Logger logger = Logger.getLogger( BaseUser.class );

    public static final String USERS = "users";

    public enum Visibility {
        VISIBLE,
        HIDDEN;
    }

    public BaseUser( Node parent, MongoDocument document ) {
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
    public void setOwner( BaseUser owner ) {
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

    public static abstract class BaseUserDescriptor<T extends BaseUser<T>> extends NodeDescriptor<T> {

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
        public T newInstance( String title ) throws ItemInstantiationException {
            BaseUser u = super.newInstance( title );
            u.getDocument().set( "username", title );
            u.getDocument().set( "_id", title );

            return (T) u;
        }

        @Override
        public void save( Request request, Response response ) {
            logger.debug( "Saving " + this );

            testString = "The millis: " + System.currentTimeMillis();
        }
    }
}
