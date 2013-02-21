package org.seventyeight.database.mongodb;

import com.mongodb.BasicDBObject;
import org.apache.log4j.Logger;

/**
 * @author cwolfgang
 *         Date: 19-02-13
 *         Time: 21:40
 */
public class MongoUpdate {

    private static Logger logger = Logger.getLogger( MongoUpdate.class );

    private MongoDBCollection collection;

    private BasicDBObject criteria = new BasicDBObject();
    private BasicDBObject update = new BasicDBObject();

    private boolean multi = false;
    private boolean upsert = false;

    public MongoUpdate( MongoDBCollection collection ) {
        this.collection = collection;
    }

    public void update() {
        BasicDBObject u = new BasicDBObject();
        if( update.isEmpty() ) {

        } else {
            logger.debug( "Criteria: " + criteria );
            logger.debug( "Update  : " + update );
            collection.getCollection().update( criteria, update, upsert, multi );
        }
    }

    public MongoUpdate addCriteria( String key, Object value ) {
        criteria.append( key, value );

        return this;
    }

    public MongoUpdate set( String key, Object value ) {
        return update( "$set", key, value );
    }

    public MongoUpdate pull( String key, Object value ) {
        return update( "$pull", key, value );
    }

    public MongoUpdate unset( String key ) {
        return update( "$unset", key, 1 );
    }

    public MongoUpdate unset( String key, int index ) {
        return update( "$unset", key + "." + index, 1 );
    }

    private MongoUpdate update( String type, String key, Object value ) {
        BasicDBObject set = null;
        if( update.containsField( type ) ) {
            set = (BasicDBObject) update.get( type );
        } else {
            set = new BasicDBObject();
            update.put( type, set );
        }

        System.out.println( "SET: " + set );

        if( value instanceof MongoDocument ) {
            set.append( key, ((MongoDocument)value).getDBObject() );
        } else {
            set.append( key, value );
        }

        return this;
    }

    public MongoUpdate setMulti() {
        multi = true;

        return this;
    }
    
    public MongoUpdate setUpsert() {
        upsert = true;
        
        return this;
    }
}
