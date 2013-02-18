package org.seventyeight.database.mongodb;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;

/**
 * @author cwolfgang
 *         Date: 16-02-13
 *         Time: 21:45
 */
public class MongoDBCollection {

    private static Logger logger = Logger.getLogger( MongoDBCollection.class );

    private DBCollection collection;

    public MongoDBCollection( DBCollection collection ) {
        this.collection = collection;
    }

    public MongoDBCollection add( MongoDocument document ) {
        collection.insert( document.getDBObject() );

        return this;
    }

    public void listDocuments() {
        logger.debug( "Number: " + collection.getCount() );
    }

    public void show() {
        DBCursor cursor = collection.find();

        while( cursor.hasNext() ) {
            DBObject d = cursor.next();
            logger.info( d );
        }
    }
}
