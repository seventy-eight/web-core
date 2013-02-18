package org.seventyeight.database.mongodb;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.seventyeight.database.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 *         Date: 15-02-13
 *         Time: 21:00
 */
public class MongoDocument implements Document {

    private BasicDBObject document;

    public MongoDocument() {
        document = new BasicDBObject();
    }

    public MongoDocument( BasicDBObject document ) {
        this.document = document;
    }

    public BasicDBObject getDBObject() {
        return document;
    }

    @Override
    public <T> T get( String key ) {
        return (T) document.get( key );
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

        if( value instanceof MongoDocument ) {
            list.add( ((MongoDocument)value).getDBObject() );
        } else {
            list.add( value );
        }

        return this;
    }

    public List<MongoDocument> getList( String key ) {
        List<BasicDBObject> list = (List<BasicDBObject>) document.get ( key );

        List<MongoDocument> docs = new ArrayList<MongoDocument>( list.size() );

        for( BasicDBObject o : list ) {
            docs.add( new MongoDocument( o ) );
        }

        return docs;
    }

    public MongoDocument addExtension( MongoDocument extensionData ) {
        document.put( EXTENSIONS, extensionData );

        return this;
    }

    @Override
    public String toString() {
        return document.toString();
    }
}
