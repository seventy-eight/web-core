package org.seventyeight.database.mongodb.tests;

import org.junit.Rule;
import org.junit.Test;
import org.seventyeight.database.mongodb.*;

/**
 * @author cwolfgang
 *         Date: 16-02-13
 *         Time: 21:12
 */
public class TestMongoDBQuery {

    public static final String COLLECTION_NAME = "webtest";

    @Rule
    public MongoDBRule env = new MongoDBRule( "testdb" );

    @Test
    public void test1() {
        MongoDBCollection collection = env.getDatabase().createCollection( COLLECTION_NAME );

        MongoDocument d = new MongoDocument();
        d.set( "name", "Svenne" );
        d.set( "age", 10 );
        collection.save( d );

        MongoDocument d2 = new MongoDocument();
        d2.set( "name", "Hans" );
        d2.set( "age", 9 );
        collection.save( d2 );

        MongoDocument d3 = new MongoDocument();
        d3.set( "name", "Hansine" );
        d3.set( "age", 15 );
        collection.save( d3 );

        collection.show();

        MongoDBQuery q = new MongoDBQuery().regex( "name", "^Hans.*$" );
        System.out.println( collection.find( q ) );

        MongoDBQuery q2 = new MongoDBQuery().greaterThan( "age", 10 );
        System.out.println( collection.find( q2 ) );

        MongoDBQuery q3 = new MongoDBQuery().regex( "name", "^Hans.*$" ).greaterThan( "age", 12 );
        System.out.println( collection.find( q3 ) );

        MongoDBQuery q4 = new MongoDBQuery().is( "name", "Svenne" );
        System.out.println( collection.find( q4 ) );

    }
}
