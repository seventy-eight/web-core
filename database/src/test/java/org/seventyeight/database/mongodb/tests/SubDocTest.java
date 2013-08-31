package org.seventyeight.database.mongodb.tests;

import org.junit.Rule;
import org.junit.Test;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBRule;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;

import static org.junit.Assert.fail;

/**
 * @author cwolfgang
 */
public class SubDocTest {

    public static final String COLLECTION_NAME = "webtest";

    @Rule
    public MongoDBRule env = new MongoDBRule( "testdb" );

    @Test
    public void test() {
        MongoDBCollection collection = env.getDatabase().getCollection( COLLECTION_NAME );

        String type = "action";
        String name = "info";
        MongoDocument document = new MongoDocument();

        MongoDocument doc = document.getr( "extensions", type, name );


        doc.set( "key1", "value1" );

        System.out.println( "DOC: " + document );

        if( doc == null ) {
            fail( "NULL, mand!" );
        }
    }

    @Test
    public void test2() {
        MongoDBCollection collection = env.getDatabase().getCollection( COLLECTION_NAME );

        String type = "action";
        String name = "info";
        MongoDocument document = new MongoDocument();

        MongoDocument doc = document.getr( "extensions", type, name );
        doc.set( "k", "v" );

        MongoDocument doc2 = document.getr( "extensions", type, "signature" );
        doc2.set( "k2", "v2" );

        //doc.set( type, new MongoDocument(  ).setList( "info" ) );
        //doc.addToList( type, new MongoDocument(  ).set( name, new MongoDocument(  ) ) );
        //doc.addToList( type, new MongoDocument(  ).set( "signature", new MongoDocument(  ) ) );
        //doc.set( type, new MongoDocument(  ).set( "signature", new MongoDocument(  ) ) );

        System.out.println( "DOC: " + document );

        if( doc == null ) {
            fail( "NULL, mand!" );
        }
    }
}
