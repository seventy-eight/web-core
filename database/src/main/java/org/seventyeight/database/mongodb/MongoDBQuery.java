package org.seventyeight.database.mongodb;

import com.mongodb.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author cwolfgang
 */
public class MongoDBQuery {
    private QueryBuilder query = new QueryBuilder();

    public MongoDBQuery is( String field, Object value ) {
        if( value instanceof MongoDocument ) {
            query.put( field ).is( ((MongoDocument)value).getDBObject() );
        } else {
            query.put( field ).is( value );
        }

        return this;
    }

    public MongoDBQuery getId( String id ) {
        query.put( "_id" ).is( id );

        return this;
    }

    public MongoDBQuery getId2( String id ) {
        query.put( "_id" ).is( new ObjectId( id ) );

        return this;
    }

    public MongoDBQuery elemMatch( String field, MongoDocument value ) {
        query.put( field ).elemMatch( value.getDBObject() );

        return this;
    }

    public MongoDBQuery exists( String field ) {
        query.put( field ).exists( true );

        return this;
    }

    public MongoDBQuery greaterThan( String field, Object value ) {
        if( value instanceof MongoDocument ) {
            query.put( field ).greaterThan( ((MongoDocument)value).getDBObject() );
        } else {
            query.put( field ).greaterThan( value );
        }

        return this;
    }

    public MongoDBQuery lessThan( String field, Object value ) {
        if( value instanceof MongoDocument ) {
            query.put( field ).lessThan( ((MongoDocument)value).getDBObject() );
        } else {
            query.put( field ).lessThan( value );
        }

        return this;
    }

    public MongoDBQuery greaterThanEquals( String field, Object value ) {
        if( value instanceof MongoDocument ) {
            query.put( field ).greaterThanEquals( ((MongoDocument)value).getDBObject() );
        } else {
            query.put( field ).greaterThanEquals( value );
        }

        return this;
    }

    public MongoDBQuery lessThanEquals( String field, Object value ) {
        if( value instanceof MongoDocument ) {
            query.put( field ).lessThanEquals( ((MongoDocument)value).getDBObject() );
        } else {
            query.put( field ).lessThanEquals( value );
        }

        return this;
    }

    public <T> MongoDBQuery in( String field, List<T> items ) {
        if( items != null && !items.isEmpty() ) {
            query.put( field ).in( items );
        }

        return this;
    }

    public <T> MongoDBQuery addIn( String field, T item ) {
        //System.out.println("FIRST: " + (DBObject)query.get());
        if( query.get().containsField( field ) ) {
            System.out.println(((DBObject)query.get().get( field )).get( "$in" ));
            //BasicDBList list = (BasicDBList) query.get().get( field );
            Object object = ((DBObject)query.get().get( field )).get( "$in" );
            System.out.println("TYPR: " + object.getClass());
            if( object instanceof BasicDBList ) {
                BasicDBList list = (BasicDBList) object;
                list.add( item );
            } else {
                BasicDBList list = new BasicDBList();
                list.add( object );
                list.add( item );
                ((DBObject)query.get().get( field )).put( "$in", list );
            }
            //System.out.println("LIST: " + list);
            //list.add( item );

        } else {
            BasicDBList list = new BasicDBList();
            list.add( item );
            query.put( field ).in( list );
        }

        //System.out.println("SECOND: " + (DBObject)query.get());

        return this;
    }

    public <T> MongoDBQuery notIn( String field, List<T> items ) {
        if( items != null && !items.isEmpty() ) {
            query.put( field ).notIn( items );
        }

        return this;
    }

    public MongoDBQuery regex( String field, String regex ) {
        query.put( field ).regex( Pattern.compile( regex ) );

        return this;
    }

    public MongoDBQuery regex( String field, Pattern pattern ) {
        query.put( field ).regex( pattern );

        return this;
    }

    public DBObject getDocument() {
        return query.get();
    }

    @Override
    public String toString() {
        return query.get().toString();
    }
}
