package org.seventyeight.web;

import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.AbstractItem;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.model.Entity;
import org.seventyeight.web.model.EntityDescriptor;
import org.seventyeight.web.model.ItemInstantiationException;

import java.util.List;

/**
 * @author cwolfgang
 *         Date: 18-02-13
 *         Time: 22:28
 */
public class User extends Entity {

    public static final String USERS = "users";

    public User( MongoDocument document ) {
        super( document );
    }

    public void setUsername( String username ) {
        document.set( "username", username );
    }

    public String getUsername() {
        return document.get( "username" );
    }

    public String getPassword() {
        return document.get( "password" );
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
