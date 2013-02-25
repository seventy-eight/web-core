package org.seventyeight.web.handlers;

import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.seventyeight.utils.ClassUtils;
import org.seventyeight.web.Core;
import org.seventyeight.web.User;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.utilities.JsonUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author cwolfgang
 *         Date: 17-01-13
 *         Time: 14:10
 */
public class TopLevelGizmoHandler {

    private static Logger logger = Logger.getLogger( TopLevelGizmoHandler.class );

    // (0)/(1)handlers/(2)first((3)second/(n)last
    // n is either an actions index or an action method

    public void execute( TopLevelGizmo gizmo, Request request, Response response ) throws GizmoException, AuthorizationException {
        if( gizmo instanceof ItemType ) {
            handleItemType( (ItemType) gizmo, request, response );
        } else if( gizmo instanceof TopLevelAction ) {
            handleTopLevelAction( (TopLevelAction) gizmo, request, response );
        } else if( gizmo instanceof TopLevelExecutor ) {
            handleExecutor( (TopLevelExecutor) gizmo, request, response );
        } else {
            throw new GizmoException( "WHAT??? " + gizmo );
        }
    }

    private void handleExecutor( TopLevelExecutor executor, Request request, Response response ) {
        executor.execute( request, response );
    }

    private void handleTopLevelAction( TopLevelAction action, Request request, Response response ) throws GizmoException {
        actions( action, 2, request, response );
    }

    private void handleItemType( ItemType type, Request request, Response response ) throws GizmoException, AuthorizationException {
        if( request.getRequestParts().length > 2 ) {
            String name = request.getRequestParts()[2];
            AbstractModelObject modelObject = null;
            modelObject = type.getItem( name );

            /* Authorization */
            checkAuthorization( modelObject, request.getUser(), Authorizer.Authorization.get( request.isRequestPost() ) );

            request.getContext().put( "title", modelObject.getDisplayName() );

            if( modelObject instanceof Actionable ) {
                actions( modelObject, 3, request, response );
            } else {
                if( request.getRequestParts().length > 2 ) {
                    throw new GizmoException( "No such action, " + request.getRequestURI() );
                } else {
                    executeThing( request, response, modelObject, "index" );
                }
            }

        } else {
            /* TODO, what? */
        }
    }

    private void checkAuthorization( ModelObject modelObject, User user, Authorizer.Authorization requiredAuthorization ) throws GizmoException, AuthorizationException {
        logger.debug( "[Authorization check] "  + user + " for " + modelObject );
        if( modelObject instanceof Authorizable ) {
            Authorizable a = (Authorizable) modelObject;
            Authorizer authorizer = a.getAuthorizer();

            logger.debug( authorizer.getAuthorization( user ).ordinal() + " >= " + requiredAuthorization.ordinal() );

            if( authorizer.getAuthorization( user ).ordinal() < requiredAuthorization.ordinal() ) {
                throw new GizmoException( user + " was not authorized" );
            }
        }
    }

    public void actions( ModelObject modelObject, int uriStart, Request request, Response response ) throws GizmoException {

        int i = uriStart;
        int l = request.getRequestParts().length;
        Action action = null;
        ModelObject lastModelObject = modelObject;
        String urlName = "index";
        for( ; i < l ; i++ ) {
            urlName = request.getRequestParts()[i];
            logger.debug( "Url name is " + urlName );

            lastModelObject = modelObject;
            action = null;

            if( modelObject instanceof Actionable ) {
                for( Action a : ((Actionable) modelObject ).getActions() ) {
                    logger.debug( "Action is " + a );
                    if( a.getUrlName().equals( urlName ) ) {
                        action = a;
                        break;
                    }
                }
                modelObject = (ModelObject) action;
            } else {
                i++;
                break;
            }

            if( action == null ) {
                logger.debug( "Action was null, breaking" );
                i++;
                break;
            }
        }

        logger.debug( "[Action method] " + urlName + " -> " + action + "/" + lastModelObject );

        if( action != null ) {
            /* Last sub space was an action, do its index method */
            logger.debug( "Action was NOT null" );
            executeThing( request, response, action, "index" );
        } else {
            // i:3  l:3
            //if( i == l - 1 ) {
            if( i == l ) {
                /* We came to an end */
                logger.debug( "Action was null" );
                executeThing( request, response, lastModelObject, urlName );

            } else {
                throw new GizmoException( urlName + " not defined for " + lastModelObject );
            }
        }

    }

    private void executeThing( Request request, Response response, ModelObject modelObject, String urlName ) throws GizmoException {
        logger.debug( "EXECUTE: " + modelObject + ", " + urlName );

        /* First try to find a view, if not a POST */
        try {
            logger.debug( "ModelObject: " + modelObject + " -> " + urlName );
            executeMethod( modelObject, request, response, urlName );
            return;
        } catch( Exception e ) {
            e.printStackTrace();
            logger.debug( modelObject + " does not not have " + urlName + ", " + e.getMessage() );
        }

            logger.debug( "TRYING VIEW FILE" );

        if( !request.isRequestPost() ) {
            try {
                request.getContext().put( "content", Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( modelObject, urlName + ".vm" ) );
                response.getWriter().print( Core.getInstance().getTemplateManager().getRenderer( request ).render( request.getTemplate() ) );
                return;
            } catch( Exception e ) {
                logger.debug( "Unable to view " + urlName + " for " + modelObject + ": " + e.getMessage() );
                throw new GizmoException( e );
            }
        }
    }

    private void executeMethod( ModelObject modelObject, Request request, Response response, String actionMethod ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        logger.debug( "METHOD: " + modelObject + ", " + actionMethod );

        Method method = getRequestMethod( modelObject, actionMethod );
        logger.debug( "FOUND METHOD: " + method );

        if( request.isRequestPost() ) {
            JsonObject json = null;
            try {
                json = JsonUtils.getJsonFromRequest( request );
            } catch ( Exception e ) {
                logger.debug( e.getMessage() );
            }
            method.invoke( modelObject, request, response, json );
        } else {
            method.invoke( modelObject, request, response );
        }
    }

    private Method getRequestMethod( ModelObject modelObject, String method ) throws NoSuchMethodException {
        String m = "do" + method.substring( 0, 1 ).toUpperCase() + method.substring( 1, method.length() );
        logger.debug( "Method: " + method + " = " + m );
        return ClassUtils.getEnheritedMethod( modelObject.getClass(), m, Request.class, Response.class );
    }
}
