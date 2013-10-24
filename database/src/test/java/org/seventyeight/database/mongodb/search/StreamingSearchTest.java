package org.seventyeight.database.mongodb.search;

import org.junit.Rule;
import org.junit.Test;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBRule;
import org.seventyeight.database.mongodb.MongoDocument;

/**
 * @author cwolfgang
 */
public class StreamingSearchTest {

    @Rule
    public MongoDBRule env = new MongoDBRule( "testdb" );

    @Test
    public void test() {
        /* Create property A */
        addProperty( "propA", "user1", "A", "1" );
        addProperty( "propA", "user1", "A", "1" );
        addProperty( "propA", "user1", "A", "1" );
        addProperty( "propA", "user1", "A", "1" );
    }

    public void addProperty( String collectionName, String username, String property, String value ) {
        MongoDBCollection collection = env.getDatabase().getCollection( collectionName );
        collection.save( createProperty( username, property, value ) );
    }

    protected MongoDocument createProperty( String username, String property, String value ) {
        return new MongoDocument().set( property, value ).set( "username", username );
    }
}
