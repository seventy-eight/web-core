package org.seventyeight.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.actions.Get;
import org.seventyeight.web.actions.ResourceAction;
import org.seventyeight.web.authentication.Authentication;
import org.seventyeight.web.authentication.AuthenticationException;
import org.seventyeight.web.authentication.Session;
import org.seventyeight.web.handlers.template.TemplateManager;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author cwolfgang
 */
public class Root implements TopLevelNode, RootNode, Parent {

    private static Logger logger = LogManager.getLogger(Root.class);

    /**
     * The Map of top level {@link Node}s
     */
    protected ConcurrentMap<String, Node> children = new ConcurrentHashMap<String, Node>();

    @Override
    public void initialize( Core core ) {
        /* Mandatory */
        //children.put( "get", new Get( core, this ) );  // This
        children.put( "resource", new ResourceAction( core ) ); // Or that?
    }

    @Override
    public void save() {
      /* Implementation is a no op */
    }

    @Override
    public String getIdentifier() {
        return "root";
    }

    @Override
    public Node getParent() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "root";
    }

    @Override
    public String getMainTemplate() {
        return TemplateManager.getUrlFromClass( Root.class, "main.vm" );
    }

    @Override
    public Node getChild( String name ) {
        if( children.containsKey( name ) ) {
            return children.get( name );
        } else {
            return null;
        }
    }

    public void addNode( String urlName, Node node ) {
        children.put( urlName, node );
    }




    @PostMethod
    public void doLogin( Request request, Response response ) throws AuthenticationException, IOException {

        String username = request.getValue( Authentication.__NAME_KEY );
        String password = request.getValue( Authentication.__PASS_KEY );
        logger.debug( "U: " + username + ", P:" + password );

        Core core = request.getCore();

        Session session = core.getAuthentication().login( username, password );

        session.save();

        Cookie c = new Cookie( Authentication.__SESSION_ID, session.getIdentifier() );
        c.setMaxAge( session.getTimeToLive() );
        response.addCookie( c );

        response.sendRedirect( request.getValue( "url", "/" ) );
    }

    public void doLogout( Request request, Response response ) throws IOException {
        logger.debug( "Logging out" );
        Core core = request.getCore();

        for( Cookie cookie : request.getCookies() ) {
            logger.debug( "Cookie: " + cookie.getName() + "=" + cookie.getValue() );
            if( cookie.getName().equals( Authentication.__SESSION_ID ) ) {
                cookie.setMaxAge( 0 );
                response.addCookie( cookie );

                /* Remove the session object */
                core.getSessionManager().removeSession( cookie.getValue() );

                break;
            }
        }

        response.sendRedirect( "/" );
    }

    @Override
    public void deleteChild( Node node ) {
        logger.debug( "DELETING {}", node );
    }
}
