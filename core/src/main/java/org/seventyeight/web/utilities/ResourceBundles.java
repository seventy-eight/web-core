package org.seventyeight.web.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author cwolfgang
 */
public class ResourceBundles {

    private static Logger logger = LogManager.getLogger( ResourceBundles.class );

    private ConcurrentMap<Locale, ConcurrentMap<Class, ResourceBundle>> map = new ConcurrentHashMap<Locale, ConcurrentMap<Class, ResourceBundle>>(  );

    public ResourceBundle getBundle( Class<?> clazz, Locale locale ) {

        /* Locale check */
        if( !map.containsKey( locale ) ) {
            logger.debug( "Adding {} to resource bundle.", locale );
            map.put( locale, new ConcurrentHashMap<Class, ResourceBundle>(  ) );
        }

        ConcurrentMap<Class, ResourceBundle> crmap = map.get( locale );
        if( !crmap.containsKey( clazz ) ) {
            logger.debug( "Adding {} to resource bundles.", clazz );
            crmap.put( clazz, loadBundle( clazz, locale ) );
        }

        return crmap.get( clazz );
    }

    public String getString( String message, Class<?> clazz, Locale locale, String ... args ) {
        ResourceBundle bundle = getBundle( clazz, locale );

        String string;
        if( bundle != null ) {
            string = bundle.getString( message );
        } else {
            string = message;
        }

        return string;
    }


    public static ResourceBundle loadBundle( Class<?> clazz, Locale locale ) {
        try {
            return ResourceBundle.getBundle( "templates.default." + clazz.getName() + ".messages", locale );
        } catch( MissingResourceException e ) {
            // Can I live without it?
            return null;
        }
    }
}
