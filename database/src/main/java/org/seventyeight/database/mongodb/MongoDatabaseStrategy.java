package org.seventyeight.database.mongodb;

import org.seventyeight.cache.DBStrategy;

/**
 * @author cwolfgang
 */
public class MongoDatabaseStrategy implements DBStrategy {

    private String collectionName;
    private MongoDBCollection collection;

    public MongoDatabaseStrategy( String collectionName ) {
        this.collectionName = collectionName;
        this.collection = MongoDBCollection.get( collectionName );
    }

    public Object get( String id ) {
        MongoDBQuery query = new MongoDBQuery().getId( id );
        MongoDocument doc = collection.findOne( query );
        if(doc != null && !doc.isNull()) {
            return doc.copy();
        } else {
            return null;
        }
    }

    public Object serialize( Object object ) {
        return ((MongoDocument)object).copy();
    }

    public Object deserialize( Object record ) {
    	return ((MongoDocument)record).copy();
    }

    public Object save( Object object, String id ) {
        if(object instanceof MongoDocument) {
            collection.save( (MongoDocument) object );
            return object;
        } else {
            throw new IllegalArgumentException( object + " not of type MongoDocument" );
        }
    }
}
