package org.seventyeight.web.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.utils.ClassUtils;
import org.seventyeight.utils.DeleteMethod;
import org.seventyeight.utils.GetMethod;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.utils.PutMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author cwolfgang
 */
public class ExecuteUtils {

    public static Logger logger = LogManager.getLogger( ExecuteUtils.class );

    private ExecuteUtils() {

    }

    public static void execute( Request request, Response response, Object object, String urlName ) throws Exception {
        execute( request, response, object, urlName, object.getClass() );
    }

    public static void execute( Request request, Response response, Object object, String urlName, Class<?> imposter ) throws Exception {
        logger.debug( "Executing: {}, {}", object, urlName );

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
            logger.debug( "{} does not have {}, {} ", object, urlName, e.getMessage() );
        }

        if( !request.isRequestPost() ) {
            render( request, response, object, urlName, imposter );
        }
    }

    public static void render( Request request, Response response, Object object, String method ) throws NotFoundException, TemplateException, IOException {
        render( request, response, object, method, object.getClass() );
    }

    public static void render( Request request, Response response, Object object, String method, Class<?> imposter ) throws NotFoundException, TemplateException, IOException {
        Core core = request.getCore();
        request.getContext().put( "content", core.getTemplateManager().getRenderer( request ).renderClass( object, imposter, method + ".vm" ) );
        response.getWriter().print( core.getTemplateManager().getRenderer( request ).render( request.getTemplate() ) );
    }

    private static void executeMethod( Object object, Request request, Response response, String actionMethod ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        logger.debug( "Executing method : {} on {}", actionMethod, object );

        Method method = getRequestMethod( object, actionMethod, request.getRequestMethod() );

        method.invoke( object, request, response );
    }

    private static Method getRequestMethod( Object object, String method, Request.RequestMethod requestMethod ) throws NoSuchMethodException {
        String m = "do" + method.substring( 0, 1 ).toUpperCase() + method.substring( 1, method.length() );

        switch( requestMethod ) {
        case POST:
            return ClassUtils.getInheritedAnnotatedMethod( object.getClass(), m, PostMethod.class, Request.class, Response.class );

        case GET:
            return ClassUtils.getInheritedAnnotatedMethod( object.getClass(), m, GetMethod.class, Request.class, Response.class );

        case PUT:
            return ClassUtils.getInheritedAnnotatedMethod( object.getClass(), m, PutMethod.class, Request.class, Response.class );

        case DELETE:
            return ClassUtils.getInheritedAnnotatedMethod( object.getClass(), m, DeleteMethod.class, Request.class, Response.class );

        default:
            return ClassUtils.getInheritedAnnotatedMethod( object.getClass(), m, GetMethod.class, Request.class, Response.class );
        }
    }
}
