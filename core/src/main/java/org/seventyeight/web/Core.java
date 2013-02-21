package org.seventyeight.web;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBManager;
import org.seventyeight.database.mongodb.MongoDatabase;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.loader.Loader;
import org.seventyeight.utils.ClassUtils;
import org.seventyeight.web.model.*;
import org.seventyeight.web.themes.Default;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.UnknownHostException;
import java.util.*;

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

    //
    /**
     * A map of descriptors keyed by their super class
     */
    private Map<Class<?>, Descriptor<?>> descriptors = new HashMap<Class<?>, Descriptor<?>>();

    /**
     * A map of interfaces corresponding to specific {@link Descriptor}s<br />
     * This is used to map an extension class/interface to those {@link Describable}s {@link Descriptor}s implementing them.
     */
    private Map<Class, List<Descriptor>> descriptorList = new HashMap<Class, List<Descriptor>>();

    /* Paths */
    private File path;
    private File orientdbPath;
    private File pluginsPath;
    private File uploadPath;
    private File themesPath;


    private AbstractTheme defaultTheme = new Default();

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
        collection.save( document );

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


    public void addDescriptor( Descriptor<?> descriptor ) {
        this.descriptors.put( descriptor.getClazz(), descriptor );

        List<Class<?>> interfaces = ClassUtils.getInterfaces( descriptor.getClazz() );
        for( Class<?> i : interfaces ) {
            logger.debug( "INTERFACE: " + i );
            List<Descriptor> list = null;
            if( !descriptorList.containsKey( i ) ) {
                descriptorList.put( i, new ArrayList<Descriptor>() );
            }
            list = descriptorList.get( i );

            list.add( descriptor );
        }

        /**/
        //descriptor.configureIndex( db );
    }

    public Descriptor<?> getDescriptor( String className ) throws ClassNotFoundException {
        return getDescriptor( Class.forName( className ) );
    }

    public <T extends Descriptor> T getDescriptor( Class<?> clazz ) {
        logger.debug( "Getting descriptor for " + clazz );

        if( descriptors.containsKey( clazz ) ) {
            return (T) descriptors.get( clazz );
        } else {
            return null;
        }
    }

    public List<Descriptor> getExtensionDescriptors( String clazz ) throws ClassNotFoundException {
        return getExtensionDescriptors( Class.forName( clazz ) );
    }

    /**
     * Get a list of {@link Descriptor}s whose {@link Describable} implements the given interface
     * @param clazz The interface in question
     * @return
     */
    public List<Descriptor> getExtensionDescriptors( Class clazz ) {
        if( descriptorList.containsKey( clazz ) ) {
            return descriptorList.get( clazz );
        } else {
            return Collections.emptyList();
        }
    }


    public AbstractTheme getDefaultTheme() {
        return defaultTheme;
    }
}
