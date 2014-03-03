package org.seventyeight.web.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.Core;

import java.text.MessageFormat;
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

    private ConcurrentMap<Locale, ConcurrentMap<String, ResourceBundle>> map = new ConcurrentHashMap<Locale, ConcurrentMap<String, ResourceBundle>>(  );

    public ResourceBundle getBundle( String className, Locale locale ) {

        /* Locale check */
        if( !map.containsKey( locale ) ) {
            logger.debug( "Adding {} to resource bundle.", locale );
            map.put( locale, new ConcurrentHashMap<String, ResourceBundle>(  ) );
        }

        ConcurrentMap<String, ResourceBundle> crmap = map.get( locale );
        if( !crmap.containsKey( className ) ) {
            logger.debug( "Adding {} to resource bundles.", className );
            logger.debug( "CRMAP: {}", crmap );
            logger.debug( "Classname: {}, locale: {}", className, locale );
            ResourceBundle rb = loadBundle( className, locale );
            if(rb != null) {
                crmap.put( className, rb );
            }
        }

        return crmap.get( className );
    }

    public String getString( String message, Class<?> clazz, Locale locale, String ... args ) {
        return getString( message, "templates.default.desktop." + clazz.getName(), locale, args );
    }

    public String getString( String message, String clazz, Locale locale, String ... args ) {
        ResourceBundle bundle = getBundle( clazz, locale );

        String string;
        if( bundle != null ) {
            string = bundle.getString( message );
        } else {
            string = message;
        }

        return MessageFormat.format( string, args );
    }


    public static ResourceBundle loadBundle( String className, Locale locale ) {
        try {
            //return ResourceBundle.getBundle( "templates.default.desktop." + className + ".messages", locale );
            //return ResourceBundle.getBundle( className + ".messages", locale );
            return ResourceBundle.getBundle( className + ".messages", locale, Core.getInstance().getClassLoader() );
        } catch( MissingResourceException e ) {
            // Can I live without it?
            return null;
        }
    }
}
