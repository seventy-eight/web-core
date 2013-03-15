package org.seventyeight.database.mongodb;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.BSONObject;
import org.seventyeight.database.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cwolfgang
 *         Date: 15-02-13
 *         Time: 21:00
 */
public class MongoDocument implements Document {

    private DBObject document;

    public MongoDocument() {
        document = new BasicDBObject();
    }

    public MongoDocument( BasicDBObject document ) {
        this.document = document;
    }

    public MongoDocument( DBObject document ) {
        this.document = document;
    }

    public DBObject getDBObject() {
        return document;
    }

    @Override
    public <T> T get( String key ) {
        Object data = (T) document.get( key );
        if( data instanceof BasicDBList ) {
            return (T) getList( (BasicDBList) data );
        } else if( data instanceof DBObject ) {
            return (T) new MongoDocument( (DBObject) data );
        } else {
            return (T) data;
        }
    }

    private List<Object> getList( BasicDBList list ) {
        if( list != null ) {
            List<Object> docs = new ArrayList<Object>( list.size() );

            for( Object o : list ) {
                docs.add( _get( o ) );
            }

            return docs;
        } else {
            return Collections.emptyList();
        }
    }

    private <T> T _get( Object o ) {
        if( o instanceof DBObject ) {
            return (T) new MongoDocument( ((DBObject)o) );
        } else {
            return (T) o;
        }
    }

    public <T> T get( String ... keys ) {
        StringBuilder key = new StringBuilder();
        key.append( keys[0] );
        for( int i = 1 ; i < keys.length ; i++ ) {
            key.append( "." );
            key.append( keys[i] );
        }
        return (T) get( key.toString() );
    }

    @Override
    public <T> T get( String key, T defaultValue ) {
        if( document.containsField( key ) ) {
            return (T) document.get( key );
        } else {
            return defaultValue;
        }
    }

    @Override
    public <T, R extends Document> R set( String key, T value ) {
        if( value instanceof MongoDocument ) {
            document.put( key, ((MongoDocument)value).getDBObject() );
        } else {
            document.put( key, value );
        }

        return (R) this;
    }

    public MongoDocument setList( String key ) {
        document.put( key, new BasicDBList() );

        return this;
    }

    public <T> MongoDocument addToList( String key, T value ) {
        BasicDBList list = (BasicDBList) document.get( key );

        if( list == null ) {
            list = new BasicDBList();
            document.put( key, list );
            //list = (BasicDBList) document.get( key );
        }
        System.out.println( "------>" + list );

        if( value instanceof MongoDocument ) {
            list.add( ((MongoDocument)value).getDBObject() );
        } else {
            list.add( value );
        }

        return this;
    }

    public List<MongoDocument> getList( String key ) {
        List<BasicDBObject> list = (List<BasicDBObject>) document.get( key );

        if( list != null ) {
            List<MongoDocument> docs = new ArrayList<MongoDocument>( list.size() );

            for( BasicDBObject o : list ) {
                docs.add( new MongoDocument( o ) );
            }

            return docs;
        } else {
            return Collections.emptyList();
        }
    }

    public MongoDocument addExtension( MongoDocument extensionData ) {
        document.put( EXTENSIONS, extensionData );

        return this;
    }

    public MongoDocument removeField( String fieldName ) {
        document.removeField( fieldName );

        return this;
    }

    @Override
    public String toString() {
        return document.toString();
    }
}
