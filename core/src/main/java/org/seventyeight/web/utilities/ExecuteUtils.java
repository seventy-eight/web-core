package org.seventyeight.web.utilities;

import org.apache.log4j.Logger;
import org.seventyeight.utils.ClassUtils;
import org.seventyeight.web.Core;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

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
        execute( request, response, object, urlName, object.getClass() );
    }

    public static void execute( Request request, Response response, Object object, String urlName, Class<?> imposter ) throws Exception {
        logger.debug( "EXECUTE: " + object + ", " + urlName );

        if( imposter == null ) {
            imposter = object.getClass();
        }

        /* First try to find a view, if not a POST */
        try {
            executeMethod( object, request, response, urlName );
            return;
        } catch( InvocationTargetException e ) {
            throw (Exception)e.getCause();
        } catch( ReflectiveOperationException e ) {
            logger.debug( object + " does not have " + urlName + ", " + e.getMessage() );
        }

        if( !request.isRequestPost() ) {
            request.getContext().put( "content", Core.getInstance().getTemplateManager().getRenderer( request ).renderClass( object, imposter, urlName + ".vm" ) );
            response.getWriter().print( Core.getInstance().getTemplateManager().getRenderer( request ).render( request.getTemplate() ) );
            return;
        }
    }

    private static void executeMethod( Object object, Request request, Response response, String actionMethod ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        logger.debug( "Executing method : " + actionMethod + " on " + object );

        Method method = getRequestMethod( object, actionMethod, request.getRequestMethod() );

        method.invoke( object, request, response );
    }

    private static Method getRequestMethod( Object object, String method, Request.RequestMethod requestMethod ) throws NoSuchMethodException {
        String m = "do" + method.substring( 0, 1 ).toUpperCase() + method.substring( 1, method.length() );

        switch( requestMethod ) {
            case POST:
            return ClassUtils.getInheritedPostMethod( object.getClass(), m, Request.class, Response.class );

        case GET:
            return ClassUtils.getInheritedMethod( object.getClass(), m, Request.class, Response.class );

        case PUT:
            return ClassUtils.getInheritedPutMethod( object.getClass(), m, Request.class, Response.class );

        default:
            return ClassUtils.getInheritedMethod( object.getClass(), m, Request.class, Response.class );
        }
    }
}
