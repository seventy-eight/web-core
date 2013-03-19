package org.seventyeight.web.nodes;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;

import java.util.List;

/**
 * @author cwolfgang
 *         Date: 26-02-13
 *         Time: 21:59
 */
public class Users implements Node {

    private static Logger logger = Logger.getLogger( Users.class );

    protected Node parent;

    public Users( Node parent ) {
        this.parent = parent;
    }

    @Override
    public Node getParent() {
        return parent;
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

    @Override
    public String getDisplayName() {
        return "Users";
    }


    public static User getUserByUsername( Node parent, String username ) throws ItemInstantiationException {
        List<MongoDocument> docs = MongoDBCollection.get( User.USERS ).find( new MongoDBQuery().is( "username", username ), 0, 1 );

        if( docs != null && !docs.isEmpty() ) {
            return new User( parent, docs.get( 0 ) );
        } else {
            throw new ItemInstantiationException( "The user " + username + " not found" );
        }
    }
}
