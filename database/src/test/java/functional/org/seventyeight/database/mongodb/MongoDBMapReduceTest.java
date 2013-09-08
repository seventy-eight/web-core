package functional.org.seventyeight.database.mongodb;

import com.mongodb.MapReduceCommand;
import org.junit.Rule;
import org.junit.Test;
import org.seventyeight.database.mongodb.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertNotNull;

/**
 * @author cwolfgang
 */
public class MongoDBMapReduceTest {

    public static final String COLLECTION_NAME = "webtest";

    @Rule
    public MongoDBRule env = new MongoDBRule( "testdb" );

    @Test
    public void testLoad() {
        /* Map */
        URL url = this.getClass().getResource( "search.activity.map.js" );
        File mapFile = new File( url.getFile() );

        /* Reduce */
        File reduceFile = new File( this.getClass().getResource( "search.activity.reduce.js" ).getFile() );

        assertNotNull( mapFile );
        assertNotNull( reduceFile );
    }

    @Test
    public void testClass() throws IOException {
        /* Map */
        File mapFile = new File( this.getClass().getResource( "search.activity.map.js" ).getFile() );

        /* Reduce */
        File reduceFile = new File( this.getClass().getResource( "search.activity.reduce.js" ).getFile() );

        MongoDBMapReduce mmr = new MongoDBMapReduce( mapFile, reduceFile );

        System.out.println(mmr.getMapFunction());

        assertNotNull( mmr.getMapFunction() );
        assertNotNull( mmr.getReduceFunction() );

    }

    @Test
    public void testClass2() throws IOException {
        /* Map */
        File mapFile = new File( this.getClass().getResource( "search.activity.map.js" ).getFile() );

        /* Reduce */
        File reduceFile = new File( this.getClass().getResource( "search.activity.reduce.js" ).getFile() );

        MongoDBMapReduce mmr = new MongoDBMapReduce( mapFile, reduceFile ).setCollection( COLLECTION_NAME );

        System.out.println(mmr.getMapFunction());

        MongoDBCollection collection = env.getDatabase().getCollection( COLLECTION_NAME );

        collection.save( createUser( "wolle" ) );
        collection.save( createUser( "robse" ) );

        collection.save( createActivity( "wolle", "a1" ) );
        collection.save( createActivity( "wolle", "a2" ) );
        collection.save( createActivity( "wolle", "a1" ) );
        collection.save( createActivity( "wolle", "a1" ) );
        collection.save( createActivity( "wolle", "a3" ) );
        collection.save( createActivity( "wolle", "a2" ) );

        mmr.execute();
    }

    @Test
    public void testClass3() throws IOException {
        /* Map */
        File mapFile = new File( this.getClass().getResource( "search.activity.map.js" ).getFile() );

        /* Reduce */
        File reduceFile = new File( this.getClass().getResource( "search.activity.reduce.js" ).getFile() );

        MongoDBMapReduce mmr = new MongoDBMapReduce( mapFile, reduceFile ).setCollection( COLLECTION_NAME ).setOutputType( MapReduceCommand.OutputType.REDUCE );

        System.out.println(mmr.getMapFunction());

        MongoDBCollection collection = env.getDatabase().getCollection( COLLECTION_NAME );

        collection.save( createUser( "wolle" ) );
        collection.save( createUser( "robse" ) );

        collection.save( createActivity( "wolle", "a1" ) );
        collection.save( createActivity( "wolle", "a2" ) );
        collection.save( createActivity( "wolle", "a1" ) );
        collection.save( createActivity( "wolle", "a1" ) );
        collection.save( createActivity( "wolle", "a3" ) );
        collection.save( createActivity( "wolle", "a2" ) );

        mmr.execute( "searchresult" );

        List<MongoDocument> docs = MongoDBCollection.get( "searchresult" ).find( new MongoDBQuery() );
        System.out.println(docs);
    }

    @Test
    public void testJoin() throws IOException {
        /* Map */
        File mapFile = new File( this.getClass().getResource( "search.plusone.map.js" ).getFile() );
        File mapFile2 = new File( this.getClass().getResource( "search.plusone2.map.js" ).getFile() );

        /* Reduce */
        File reduceFile = new File( this.getClass().getResource( "search.plusone.reduce.js" ).getFile() );
        File reduceFile2 = new File( this.getClass().getResource( "search.plusone2.reduce.js" ).getFile() );

        MongoDBMapReduce mmr = new MongoDBMapReduce( mapFile, reduceFile ).setCollection( COLLECTION_NAME ).setOutputType( MapReduceCommand.OutputType.REDUCE );

        MongoDBCollection collection = env.getDatabase().getCollection( COLLECTION_NAME );
        MongoDBCollection pluses = env.getDatabase().getCollection( "pluses" );

        collection.save( createUser( "wolle" ) );
        collection.save( createUser( "robse" ) );
        collection.save( createUser( "aee" ) );
        collection.save( createUser( "luderbo" ) );
        collection.save( createUser( "jæns" ) );
        collection.save( createUser( "kallum" ) );

        pluses.save( createPlusOne( "wolle", "robse" ) );
        pluses.save( createPlusOne( "wolle", "robse" ) );
        pluses.save( createPlusOne( "wolle", "robse" ) );
        pluses.save( createPlusOne( "aee", "robse" ) );
        pluses.save( createPlusOne( "aee", "robse" ) );
        pluses.save( createPlusOne( "jæns", "robse" ) );

        mmr.execute( "searchresult" );

        List<MongoDocument> docs = MongoDBCollection.get( "searchresult" ).find( new MongoDBQuery() );
        System.out.println(docs);

        MongoDBMapReduce mmr2 = new MongoDBMapReduce( mapFile2, reduceFile2 ).setCollection( "pluses" ).setOutputType( MapReduceCommand.OutputType.REDUCE );
        mmr2.execute( "searchresult" );
        List<MongoDocument> docs2 = MongoDBCollection.get( "searchresult" ).find( new MongoDBQuery() );
        System.out.println(docs2);
    }


    protected MongoDocument createUser( String username ) {
        return new MongoDocument().set( "type", "user" ).set( "username", username ).set( "email", username + "@mail.dk" );
    }

    protected MongoDocument createActivity( String username, String activity ) {
        return new MongoDocument().set( "type", "activity" ).set( "username", username ).set( "activity", activity ).set( "date", new Date() );
    }

    protected MongoDocument createPlusOne( String username, String from ) {
        return new MongoDocument().set( "username", username ).set( "from", from ).set( "date", new Date() );
    }
}
