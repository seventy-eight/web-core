package org.seventyeight.database.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author cwolfgang
 *         Date: 16-02-13
 *         Time: 20:54
 */
public class MongoDatabase {

    private static Logger logger = LogManager.getLogger( MongoDatabase.class );

    private DB db;

    public MongoDatabase( DB db ) {
        this.db = db;
    }

    public MongoDBCollection getCollection( String name ) {
        logger.debug( "Getting collection " + name );
        DBCollection collection = db.getCollection( name );

        return new MongoDBCollection( collection );
    }

    public List<DBCollection> getCollections() {
        logger.debug( "Getting collections" );

        Set<String> cs = db.getCollectionNames();
        List<DBCollection> collections = new ArrayList<DBCollection>( cs.size() );

        for( String c : cs ) {
            logger.debug( "Collection: " + c );
            collections.add( db.getCollectionFromString( c ) );
        }

        return collections;
    }

    public void remove() {
        logger.info( "Removing " + db );
        db.dropDatabase();
    }
}
