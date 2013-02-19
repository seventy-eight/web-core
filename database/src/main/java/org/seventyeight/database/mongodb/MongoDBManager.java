package org.seventyeight.database.mongodb;

import com.mongodb.MongoClient;

import java.net.UnknownHostException;

/**
 * @author cwolfgang
 *         Date: 16-02-13
 *         Time: 21:50
 */
public class MongoDBManager {

    private static MongoDBManager instance;

    public static int PORT = 27017;
    protected MongoClient client;
    private MongoDatabase db;

    public MongoDBManager( String databaseName ) throws UnknownHostException {
        if( instance != null ) {
            throw new IllegalStateException( "DB Manager instance already defined" );
        }

        client = new MongoClient( "localhost", PORT );
        db = new MongoDatabase( client.getDB( databaseName ) );
        instance = this;
    }

    public static MongoDBManager getInstance() {
        return instance;
    }

    public MongoDatabase getDatabase() {
        return db;
    }

    public void close() {
        //instance = null;
        //client.close();
    }
}
