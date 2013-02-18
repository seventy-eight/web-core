package org.seventyeight.web;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBManager;
import org.seventyeight.database.mongodb.MongoDatabase;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.loader.Loader;
import org.seventyeight.web.model.AbstractItem;
import org.seventyeight.web.model.Item;
import org.seventyeight.web.model.ItemInstantiationException;

import java.lang.reflect.Constructor;
import java.net.UnknownHostException;

/**
 * @author cwolfgang
 *         Date: 16-02-13
 *         Time: 23:16
 */
public class Core {

    private static Logger logger = Logger.getLogger( Core.class );

    private static Core instance;

    public static final String ITEM_COLLECTION_NAME = "items";

    private org.seventyeight.loader.ClassLoader classLoader = null;
    private Loader pluginLoader;

    private MongoDBManager dbManager;
    private MongoDatabase db;

    public Core( String dbname ) throws UnknownHostException {
        if( instance != null ) {
            throw new IllegalStateException( "Instance already defined" );
        }

        dbManager = new MongoDBManager( dbname );
        db = dbManager.getDatabase();
    }

    public static Core getInstance() {
        return instance;
    }

    public MongoDatabase getDatabase() {
        return db;
    }

    public <T extends AbstractItem> T createItem( Class<?> clazz ) throws ItemInstantiationException {
        logger.debug( "Creating " + clazz.getName() );

        MongoDBCollection collection = db.createCollection( ITEM_COLLECTION_NAME );
        MongoDocument document = new MongoDocument();

        T instance = null;
        try {
            Constructor<?> c = clazz.getConstructor( MongoDocument.class );
            instance = (T) c.newInstance( document );
        } catch( Exception e ) {
            throw new ItemInstantiationException( "Unable to instantiate " + clazz.getName(), e );
        }

        document.set( "class", clazz.getName() );
        collection.add( document );

        return instance;
    }

    public <T extends Item> T getItem( MongoDocument document ) throws ItemInstantiationException {
        String clazz = (String) document.get( "class" );

        if( clazz == null ) {
            logger.warn( "Class property not found" );
            throw new ItemInstantiationException( "\"class\" property not found for " + document );
        }
        logger.debug( "Item class: " + clazz );

        try {
            Class<Item> eclass = (Class<Item>) Class.forName(clazz, true, classLoader );
            Constructor<?> c = eclass.getConstructor( MongoDocument.class );
            return (T) c.newInstance( document );
        } catch( Exception e ) {
            logger.error( "Unable to get the class " + clazz );
            throw new ItemInstantiationException( "Unable to get the class " + clazz, e );
        }

    }
}
