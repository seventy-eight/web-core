package org.seventyeight.web;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.AbstractItem;

/**
 * @author cwolfgang
 *         Date: 18-02-13
 *         Time: 22:28
 */
public class User extends AbstractItem {

    public User( MongoDocument document ) {
        super( document );
    }

    public void setUsername( String username ) {
        document.set( "username", username );
    }

    public String getUsername() {
        return document.get( "username" );
    }

    @Override
    public String getDisplayName() {
        return getUsername();
    }

    @Override
    public String toString() {
        return "User[" + getUsername() + "]" ;
    }
}
