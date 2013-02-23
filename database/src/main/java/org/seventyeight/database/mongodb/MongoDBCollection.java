package org.seventyeight.database.mongodb;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

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

    public MongoDBCollection( String colletionName ) {
        this.collection = MongoDBManager.getInstance().getDatabase().createCollection( colletionName ).getCollection();
    }

    public static MongoDBCollection get( String colletionName ) {
        return MongoDBManager.getInstance().getDatabase().createCollection( colletionName );
    }

    public MongoDBCollection save( MongoDocument document ) {
        collection.save( document.getDBObject() );

        return this;
    }

    public DBCollection getCollection() {
        return collection;
    }

    public void listDocuments() {
        logger.debug( "Number: " + collection.getCount() );
    }


    public List<MongoDocument> find( MongoDBQuery query ) {
        List<DBObject> objs = collection.find( query.getDocument() ).toArray();
        List<MongoDocument> docs = new ArrayList<MongoDocument>( objs.size() );

        for( DBObject obj : objs ) {
            docs.add( new MongoDocument( obj ) );
        }

        return docs;
    }

    public List<MongoDocument> find( MongoDBQuery query, int offset, int limit ) {
        List<DBObject> objs = collection.find( query.getDocument() ).skip( offset ).limit( limit ).toArray();
        List<MongoDocument> docs = new ArrayList<MongoDocument>( objs.size() );

        for( DBObject obj : objs ) {
            docs.add( new MongoDocument( obj ) );
        }

        return docs;
    }

    public void remove( MongoDBQuery query ) {
        collection.remove( query.getDocument() );
    }

    public void show() {
        DBCursor cursor = collection.find();

        while( cursor.hasNext() ) {
            DBObject d = cursor.next();
            logger.info( d );
        }
    }
}
