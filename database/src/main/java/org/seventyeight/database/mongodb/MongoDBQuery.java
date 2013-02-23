package org.seventyeight.database.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author cwolfgang
 *         Date: 23-02-13
 *         Time: 10:48
 */
public class MongoDBQuery {
    private BasicDBObject query = new BasicDBObject();
    private DBCollection collection;

    public MongoDBQuery is( String field, Object value ) {
        if( value instanceof MongoDocument ) {
            query.put( field, ((MongoDocument)value).getDBObject() );
        } else {
            query.put( field, value );
        }

        return this;
    }

    public MongoDBQuery gt( String field, Object value ) {
        return q( "$gt", field, value );
    }

    public MongoDBQuery lt( String field, Object value ) {
        return q( "$lt", field, value );
    }

    public MongoDBQuery gte( String field, Object value ) {
        return q( "$gte", field, value );
    }

    public MongoDBQuery lte( String field, Object value ) {
        return q( "$lte", field, value );
    }

    private MongoDBQuery q( String type, String field, Object value ) {
        if( value instanceof MongoDocument ) {
            query.put( field, ((MongoDocument)value).getDBObject() );
        } else {
            query.put( field, new BasicDBObject( type, value ) );
        }

        return this;
    }

    public <T> MongoDBQuery in( String field, List<T> items ) {
        if( items != null && !items.isEmpty() ) {
            query.put( field, new BasicDBObject().put( "$in", items ) );
        }

        return this;
    }

    public <T> MongoDBQuery notIn( String field, List<T> items ) {
        if( items != null && !items.isEmpty() ) {
            query.put( field, new BasicDBObject().put( "$nin", items ) );
        }

        return this;
    }

    public MongoDBQuery regex( String field, String regex ) {
        query.put( field, Pattern.compile( regex ) );

        return this;
    }

    public MongoDBQuery regex( String field, Pattern pattern ) {
        query.put( field, pattern );

        return this;
    }

    public DBObject getDocument() {
        return query;
    }
}
