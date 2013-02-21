package org.seventyeight.database.mongodb.tests;

import org.junit.Rule;
import org.junit.Test;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBRule;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;

import java.util.List;

/**
 * @author cwolfgang
 *         Date: 16-02-13
 *         Time: 21:12
 */
public class TestMongoDBUpdate {

    public static final String COLLECTION_NAME = "webtest";

    @Rule
    public MongoDBRule env = new MongoDBRule( "testdb" );

    @Test
    public void test1() {
        MongoDBCollection collection = env.getDatabase().createCollection( COLLECTION_NAME );

        MongoDocument d = new MongoDocument();
        d.set( "name", "Svenne" );
        d.set( "Addresse", "Here" );
        collection.save( d );

        collection.show();

        MongoUpdate u = new MongoUpdate( collection ).set( "name", "Svenne oLotta" );
        u.update();

        collection.show();
    }

    @Test
    public void test2() {
        MongoDBCollection collection = env.getDatabase().createCollection( COLLECTION_NAME );

        MongoDocument d = new MongoDocument();
        d.set( "name", "Svenne" );
        d.set( "crap", "lotsa" );
        d.set( "Address", "Here" );
        collection.save( d );

        collection.show();

        MongoUpdate u = new MongoUpdate( collection ).set( "name", "Svenne oLotta" ).unset( "crap" );
        u.update();

        collection.show();
    }

    @Test
    public void test3() {
        MongoDBCollection collection = env.getDatabase().createCollection( COLLECTION_NAME );

        MongoDocument d = new MongoDocument();
        d.setList( "names" );

        MongoDocument n1 = new MongoDocument().set( "name", "wolle" );
        MongoDocument n2 = new MongoDocument().set( "name", "bolle" );

        d.addToList( "names", n1 );
        d.addToList( "names", n2 );

        collection.save( d );

        collection.show();

        //MongoUpdate u = new MongoUpdate( collection ).pull( "names", new MongoDocument().set( "name", "bolle" ) );
        //MongoUpdate u = new MongoUpdate( collection ).unset( "names.1" );
        MongoUpdate u = new MongoUpdate( collection ).unset( "names", 1 );
        u.update();

        collection.show();
    }
}
