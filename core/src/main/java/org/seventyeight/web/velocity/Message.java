package org.seventyeight.web.velocity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.Core;

import java.util.Locale;

/**
 * @author cwolfgang
 */
public class Message {
    private static Logger logger = LogManager.getLogger( Message.class );
    private String className;
    private Locale locale;

    private Core core;

    public Message( Core core, Class<?> clazz, Locale locale ) {
        this.className = clazz.getName();
        this.locale = locale;

        this.core = core;
    }

    public Message( Core core, String className, Locale locale ) {
        this.className = className;
        this.locale = locale;
        this.core = core;
    }

    public String get(String message, String ... args) {
        logger.debug( "Message: {}, class: {}, locale: {}", message, className, locale );
        return core.getMessages().getString( message, className, locale, args );
    }
}
