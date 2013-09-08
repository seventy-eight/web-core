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
 */
public class TestMongoDB {

    public static final String COLLECTION_NAME = "webtest";

    @Rule
    public MongoDBRule env = new MongoDBRule( "testdb" );

    @Test
    public void test1() {
        MongoDBCollection collection = env.getDatabase().getCollection( COLLECTION_NAME );

        MongoDocument d = new MongoDocument();
        d.set( "snade", "made" );
        collection.save( d );

        collection.listDocuments();

        env.getDatabase().getCollections();
    }


    @Test
    public void test2() {
        MongoDBCollection collection = env.getDatabase().getCollection( COLLECTION_NAME );

        MongoDocument d1 = new MongoDocument();
        MongoDocument d2 = new MongoDocument();

        d1.set( "CONTENT", "YEAH" );
        d2.set( "CHILD1", d1 );
        d2.set( "CHILD1", d1 );

        collection.save( d2 );

        collection.show();
    }

    @Test
    public void test3() {
        MongoDBCollection collection = env.getDatabase().getCollection( COLLECTION_NAME + "1" );

        MongoDocument parent = new MongoDocument();
        parent.setList( "childs" );

        MongoDocument c1 = new MongoDocument();
        c1.set( "val1", 1 );
        c1.set( "val2", 2 );

        parent.addToList( "childs", c1 );

        c1.set( "val1", 111 );
        parent.addToList( "childs", c1 );

        collection.save( parent );

        collection.show();

        List<MongoDocument> docs = parent.getList( "childs" );
        for( MongoDocument doc : docs ) {
            System.out.println( "DOC: " + doc);
        }
    }

    @Test
    public void test4() {
        MongoDBCollection collection = env.getDatabase().getCollection( COLLECTION_NAME + "2" );

        MongoDocument d1 = new MongoDocument();
        d1.set( "ONE", 1 );
        collection.save( d1 );

        collection.show();

        d1.set( "ONE", 2 );
        collection.save( d1 );

        collection.show();
    }

    @Test
    public void test5() {
        MongoDBCollection collection = env.getDatabase().getCollection( "groups" );

        MongoDocument d1 = new MongoDocument();
        d1.set( "name", "The gang" );
        collection.save( d1 );

        collection.show();

        collection.update( new MongoUpdate().push( "members", "wolle" ) );
        collection.update( new MongoUpdate().push( "members", "aee" ) );
        collection.update( new MongoUpdate().push( "members", "robse" ) );

        collection.show();
    }
}
