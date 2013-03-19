package org.seventyeight.web.utilities;

import com.google.gson.JsonObject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.seventyeight.utils.ClassUtils;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author cwolfgang
 */
public class ExecuteUtils {

    public static Logger logger = Logger.getLogger( ExecuteUtils.class );

    private ExecuteUtils() {

    }

    public static void execute( Request request, Response response, Object object, String urlName ) throws Exception {
        logger.debug( "EXECUTE: " + object + ", " + urlName );

        /* First try to find a view, if not a POST */
        try {
            logger.debug( "ModelObject: " + object + " -> " + urlName );
            executeMethod( object, request, response, urlName );
            return;
        } catch( InvocationTargetException e ) {
            throw (Exception)e.getCause();
        } catch( ReflectiveOperationException e ) {
            logger.debug( object + " does not not have " + urlName + ", " + e.getMessage() );
            logger.log( Level.WARN, "Failed", e );
        }

        logger.debug( "TRYING VIEW FILE" );

        if( !request.isRequestPost() ) {
            request.getContext().put( "content", Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( object, urlName + ".vm" ) );
            response.getWriter().print( Core.getInstance().getTemplateManager().getRenderer( request ).render( request.getTemplate() ) );
            return;
        }
    }

    private static void executeMethod( Object object, Request request, Response response, String actionMethod ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        logger.debug( "METHOD: " + object + ", " + actionMethod );

        Method method = getRequestMethod( object, actionMethod );
        logger.debug( "FOUND METHOD: " + method );

        method.invoke( object, request, response );
    }

    private static Method getRequestMethod( Object object, String method ) throws NoSuchMethodException {
        String m = "do" + method.substring( 0, 1 ).toUpperCase() + method.substring( 1, method.length() );
        logger.debug( "Method: " + method + " = " + m );
        return ClassUtils.getEnheritedMethod( object.getClass(), m, Request.class, Response.class );
    }
}
