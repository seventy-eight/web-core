package org.seventyeight.database.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class MongoDBCollection {

    private static Logger logger = LogManager.getLogger( MongoDBCollection.class );

    private DBCollection collection;

    public MongoDBCollection( DBCollection collection ) {
        this.collection = collection;
    }

    public MongoDBCollection( String colletionName ) {
        this.collection = MongoDBManager.getInstance().getDatabase().getCollection( colletionName ).getCollection();
    }

    public static MongoDBCollection get( String colletionName ) {
        return MongoDBManager.getInstance().getDatabase().getCollection( colletionName );
    }

    public MongoDBCollection save( MongoDocument document ) {
        logger.debug( "Saving " + document );
        collection.save( document.getDBObject() );

        return this;
    }

    public long count( MongoDBQuery query ) {
        return collection.count( query.getDocument() );
    }

    public DBCollection getCollection() {
        return collection;
    }

    public void listDocuments() {
        logger.debug( "Number: " + collection.getCount() );
    }


    public List<MongoDocument> find( MongoDBQuery query ) {
        logger.debug( "Finding with " + query.getDocument().toString() );

        List<DBObject> objs = collection.find( query.getDocument() ).toArray();
        List<MongoDocument> docs = new ArrayList<MongoDocument>( objs.size() );

        for( DBObject obj : objs ) {
            docs.add( new MongoDocument( obj ) );
        }

        return docs;
    }

    public List<MongoDocument> find( MongoDBQuery query, int offset, int limit ) {
        return find( query, offset, limit, null, new String[] {} );
    }

    public List<MongoDocument> find( MongoDBQuery query, int offset, int limit, MongoDocument sort ) {
        return find(query, offset, limit, sort, new String[] {});
    }

    public List<MongoDocument> find( MongoDBQuery query, int offset, int limit, MongoDocument sort, String ... keys ) {
        List<DBObject> objs;
        DBObject keysObj = null;
        if(keys != null) {
            keysObj = new BasicDBObject();
            for(String key : keys) {
                keysObj.put( key, true );
            }
        }
        if(sort != null && !sort.isNull()) {
            objs = collection.find( query.getDocument(), keysObj ).sort( sort.getDBObject() ).skip( offset ).limit( limit ).toArray();
        } else {
            objs = collection.find( query.getDocument(), keysObj ).skip( offset ).limit( limit ).toArray();
        }
        List<MongoDocument> docs = new ArrayList<MongoDocument>( objs.size() );

        for( DBObject obj : objs ) {
            docs.add( new MongoDocument( obj ) );
        }

        return docs;
    }

    public MongoDocument findOne( MongoDBQuery query ) {
        logger.debug( "Query is " + query.getDocument() );
        return new MongoDocument( collection.findOne( query.getDocument() ) );
    }
    
	public MongoDocument findOne( MongoDBQuery query, MongoDocument field ) {
        logger.debug( "Query is {}, fields {}", query.getDocument(), field );
        return new MongoDocument( collection.findOne( query.getDocument(), field.getDBObject() ) );
	}

    public MongoDocument findOne( MongoDBQuery query, MongoDocument field, MongoDocument sort ) {
        logger.debug( "Query is " + query.getDocument() );
        return new MongoDocument( collection.findOne( query.getDocument(), (field != null ? field.getDBObject() : null), sort.getDBObject() ) );
    }

    public MongoDocument getDocumentById( String id ) {
        BasicDBObject query = new BasicDBObject();
        //query.put( "_id", new ObjectId( id ) );
        query.put( "_id", id );
        DBObject dbObj = collection.findOne( query );
        return new MongoDocument( dbObj );
    }

    public void createIndex( String name, MongoDocument index ) {
        collection.ensureIndex( index.getDBObject(), name );
    }

    public void remove( MongoDBQuery query ) {
        collection.remove( query.getDocument() );
    }

    public void update( MongoUpdate update ) {
        update( new MongoDBQuery(), update );
    }

    public void update( MongoDBQuery query, MongoUpdate update ) {
        if( update.getDocument().isEmpty() ) {

        } else {
            logger.debug( "Criteria: " + query.getDocument() );
            logger.debug( "Update  : " + update.getDocument() );
            collection.update( query.getDocument(), update.getDocument(), update.isUpsert(), update.isMulti() );
        }
    }

    public void show() {
        DBCursor cursor = collection.find();

        while( cursor.hasNext() ) {
            DBObject d = cursor.next();
            logger.info( d );
        }
    }
}
