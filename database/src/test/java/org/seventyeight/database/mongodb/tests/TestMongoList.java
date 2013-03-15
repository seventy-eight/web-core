package org.seventyeight.database.mongodb.tests;

import com.mongodb.BasicDBObject;
import org.junit.Rule;
import org.junit.Test;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBRule;
import org.seventyeight.database.mongodb.MongoDocument;

import java.util.ArrayList;

/**
 * @author cwolfgang
 */
public class TestMongoList {

    public static final String COLLECTION_NAME = "webtest";

    @Rule
    public MongoDBRule env = new MongoDBRule( "testdb" );

    @Test
    public void test1() {
        MongoDBCollection collection = env.getDatabase().createCollection( COLLECTION_NAME );

        MongoDocument d = new MongoDocument();
        d.set( "type", "article" );

        ArrayList<Object> posts = new ArrayList<Object>();
        posts.add( new BasicDBObject().append( "name", "Svenne" ).append( "text", "text1" ) );
        posts.add( new BasicDBObject().append( "name", "MC Kogle" ).append( "text", "text2" ) );
        posts.add( new BasicDBObject().append( "name", "DJ Glatnakke" ).append( "text", "text3" ) );

        d.set( "data", new BasicDBObject().append( "posts", posts ) );

        //posts.add( new BasicDBObject().append( "brand", "Ford" ).append( "model", "s-max" ) );
        //d.set( "posts", posts );
        collection.save( d );


        collection.show();

        MongoDocument data = d.get( "data" );
        System.out.println( data.get( "posts" ).getClass() );
    }
}
