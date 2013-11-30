package org.seventyeight.web.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.seventyeight.utils.StopWatch;
import org.seventyeight.web.Core;
import org.seventyeight.web.authentication.NoAuthorizationException;
import org.seventyeight.web.authorization.ACL;
import org.seventyeight.web.authorization.AccessControlled;
import org.seventyeight.web.model.AbstractTheme;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.PersistedObject;
import org.seventyeight.web.nodes.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author cwolfgang
 */
public class Request extends HttpServletRequestWrapper implements CoreRequest {

    private static Logger logger = LogManager.getLogger( Request.class );

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

    private Locale locale = new Locale( "da", "DK" );

    private StopWatch stopWatch = null;

    public enum RequestMethod {
        GET,
        POST,
        DELETE,
        PUT
    }

    public Request( HttpServletRequest httpServletRequest ) {
        super( httpServletRequest );
        setRequestMethod( httpServletRequest.getMethod() );
        this.template = Core.getInstance().getDefaultTemplate();
    }

    public void setRequestMethod( String m ) {
        this.method = RequestMethod.valueOf( m );
    }

    public boolean isRequestPost() {
        return method.equals( RequestMethod.POST );
    }

    public RequestMethod getRequestMethod() {
        return method;
    }

    public boolean isRequestPut() {
        return method.equals( RequestMethod.PUT );
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

    public void checkPermissions( Node node, ACL.Permission permission ) throws NoAuthorizationException {
        logger.debug( "Checking authorization for " + user );

        while( node != null ) {
            logger.debug( "Checking " + node );
            if( node instanceof AccessControlled ) {
                if( ((AccessControlled)node).getACL().getPermission( user ).ordinal() < permission.ordinal() ) {
                    throw new NoAuthorizationException( user + " was not authorized to " + node );
                }
            }

            node = node.getParent();
        }

        logger.debug( "Was authorized" );
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

    @Override
    public Integer getInteger( String key ) {
        return getInteger( key, null );
    }

    @Override
    public Integer getInteger( String key, Integer defaultValue ) {
        Integer i = defaultValue;
        if( this.getParameter( key ) != null ) {
            String val = this.getParameter( key );
            try {
                i = Integer.parseInt( val );
            } catch( NumberFormatException e ) {
                /* No op, default value is used */
            }
        }

        return i;
    }

    /**
     * @deprecated use {@link getInteger} instead
     */
    public int getInt( String key, int defaultValue ) {
        if( this.getParameter( key ) != null ) {
            return Integer.parseInt( this.getParameter( key ) );
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

    public boolean isUser( User user ) {
        if( this.user != null && user != null ) {
            return this.user.equals( user );
        } else {
            return false;
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocaleFromCookie( String name) {
        Cookie cookie = getCookie( name );
        String language;
        if( cookie != null ) {
            language = cookie.getValue();
        } else {
            language = "en_US";
        }

        logger.debug( "Setting language to {}.", language );

        this.locale = new Locale( language );
    }

    public void setStopWatch( StopWatch stopWatch ) {
        this.stopWatch = stopWatch;
    }

    public StopWatch getStopWatch() {
        return stopWatch;
    }

    private Map<String, Cookie> cookies;

    public Cookie getCookie( String name ) {
        if( cookies == null ) {
            logger.debug( "Getting COOOOOOOOKEIESS:...." );
            cookies = new HashMap<String, Cookie>(  );
            for( Cookie cookie : getCookies() ) {
                logger.debug( "COOKIE {} = {}", cookie.getName(), cookie.getValue() );
                cookies.put( cookie.getName(), cookie );
            }
        }

        logger.debug( "COOKIES: " + cookies );

        return cookies.get( name );
    }
}
