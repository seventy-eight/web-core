package org.seventyeight.web.servlet;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.seventyeight.web.authentication.NoAuthorizationException;
import org.seventyeight.web.model.*;
import org.seventyeight.web.nodes.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author cwolfgang
 */
public class Request extends HttpServletRequestWrapper implements CoreRequest {

    private static Logger logger = Logger.getLogger( Request.class );

    private static final String __MULTIPART = "multipart/form-data";

    private RequestMethod method = RequestMethod.GET;
    private AbstractTheme theme = null;
    private VelocityContext context;

    private PersistedObject modelObject;

    private String template = "org/seventyeight/web/main.vm";

    private boolean transactional = false;

    private User user;
    private boolean authenticated;

    private String[] requestParts;

    public enum ResponseType {
        PAGED,
        HTTP_CODE
    }

    private ResponseType responseType = ResponseType.PAGED;

    public enum RequestMethod {
        GET,
        POST,
        DELETE,
        PUT
    }

    public Request( HttpServletRequest httpServletRequest ) {
        super( httpServletRequest );
        setRequestMethod( httpServletRequest.getMethod() );
    }

    public void setRequestMethod( String m ) {
        this.method = RequestMethod.valueOf( m );
    }

    public boolean isRequestPost() {
        return method.equals( RequestMethod.POST );
    }

    public String[] getRequestParts() {
        return requestParts;
    }

    public void setRequestParts( String[] parts ) {
        this.requestParts = parts;
    }

    @Override
    public void setModelObject( PersistedObject modelObject ) {
        this.modelObject = modelObject;
    }

    @Override
    public PersistedObject getModelObject() {
        return modelObject;
    }

    public void setTransactional( boolean t ) {
        this.transactional = t;
    }

    public boolean hasTransaction() {
        return transactional;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated( boolean authenticated ) {
        this.authenticated = authenticated;
    }

    public void checkAuthorization( Node node, Authorizer.Authorization authorization ) throws NoAuthorizationException {
        logger.debug( "Checking authorization for " + user );

        while( node != null ) {
            logger.debug( "Checking " + node );
            if( node instanceof Authorizer ) {
                checkAuthorization( (Authorizer)node, authorization );
            }

            node = node.getParent();
        }

        logger.debug( "Was authorized" );
    }

    public void checkAuthorization( Authorizer authorizer, Authorizer.Authorization authorization ) throws NoAuthorizationException {
        Authorizer.Authorization auth = null;
        try {
            auth = authorizer.getAuthorization( this.user );
        } catch( AuthorizationException e ) {
            throw new NoAuthorizationException( e );
        }

        logger.debug( "User auth: " + auth + ", required: " + authorization );

        if( auth.ordinal() >= authorization.ordinal() ) {
            return;
        } else {
            throw new NoAuthorizationException( user + " was not authorized to " + authorizer );
        }
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate( String template ) {
        this.template = template;
    }

    public void setTheme( AbstractTheme theme ) {
        this.theme = theme;
    }

    public AbstractTheme getTheme() {
        return theme;
    }

    public VelocityContext getContext() {
        return context;
    }

    public void setContext( VelocityContext context ) {
        this.context = context;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser( User user ) {
        this.user = user;
    }

    @Override
    public <T> T getValue( String key ) {
        return (T) this.getParameter( key );
    }

    @Override
    public <T> T getValue( String key, T defaultValue ) {
        if( this.getParameter( key ) != null ) {
            return (T) this.getParameter( key );
        } else {
            return defaultValue;
        }
    }

    public static boolean isMultipart( HttpServletRequest request) {
        if( request.getContentType() != null ) {
            return request.getContentType().startsWith( __MULTIPART );
        } else {
            return false;
        }
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public boolean isPagedResponseType() {
        return responseType == ResponseType.PAGED;
    }

    public void setResponseType( ResponseType responseType ) {
        this.responseType = responseType;
    }
}
