package org.seventyeight.database.mongodb;

import org.apache.log4j.Logger;
import org.seventyeight.database.Installation;
import org.seventyeight.loader.Handler;
import org.seventyeight.loader.Loader;

import java.lang.annotation.Annotation;

/**
 * @author cwolfgang
 */
public class MongoDBInstaller {


    private static Logger logger = Logger.getLogger( MongoDBInstaller.class );

    private org.seventyeight.loader.ClassLoader classLoader = null;
    private Loader loader;

    public MongoDBInstaller() {
        classLoader = new org.seventyeight.loader.ClassLoader( Thread.currentThread().getContextClassLoader() );
        this.loader = new Loader( classLoader );
    }

    public void initialize() {
        loader.put( Installation.class, new MongoDBHandler() );
        //loader.load()
    }

    private class MongoDBHandler extends Handler {
        @Override
        public void handle( Annotation a, Class<?> clazz ) throws Exception {
            logger.debug( "Handler: " + a );
        }
    }
}
