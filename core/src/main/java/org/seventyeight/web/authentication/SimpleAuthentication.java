package org.seventyeight.web.authentication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.utils.Utils;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import javax.servlet.http.Cookie;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


public class SimpleAuthentication implements Authentication {

	private static Logger logger = LogManager.getLogger( SimpleAuthentication.class );
	
	public void authenticate( Request request, Response response ) throws AuthenticationException {

		String hash = null;
		
		for( Cookie cookie : request.getCookies() ) {
			logger.debug( "Cookie: " + cookie.getName() + "=" + cookie.getValue() );
			if( cookie.getName().equals( __SESSION_ID ) ) {
				hash = cookie.getValue();
				break;
			}
		}

        Session session = null;

        if( hash == null ) {
            if( request.getValue( __NAME_KEY, null ) != null && request.getValue( __PASS_KEY, null ) != null ) {
                String username = request.getValue( __NAME_KEY );
                String password = request.getValue( __PASS_KEY );
                logger.debug( "U: " + username + ", P:" + password );
                try {
                    session = login( username, password );
                } catch( Exception e ) {
                    /* Never mind, just move along... */
                }
            }
        } else {
			logger.debug( "Found hash: " + hash );
			session = Core.getInstance().getSessionManager().getSession( hash );
		}

        if( session != null ) {
            User user = User.getUserByUsername( Core.getInstance(), session.getUser() );
            if( user != null ) {
                logger.debug( "Session user is " + user );
                request.setAuthenticated( true );
                request.setUser( user );
                return;
            } else {
                logger.debug( "NOT VALID USER" );
            }
        }
	}

    public Session login( String username, String password ) throws AuthenticationException {
        if( !username.isEmpty() ) {
            User user = User.getUserByUsername( null, username );

            if( user == null ) {
                throw new AuthenticationException( "The user " + username + " does not exist!" );
            }

            String hashed = "";
            try {
                hashed = Utils.md5( password );
            } catch( NoSuchAlgorithmException e ) {
                throw new AuthenticationException( e );
            }
            if( user.getPassword() != null && !hashed.equals( user.getPassword() ) ) {
                logger.debug( "Wrong password" );
                throw new AuthenticationException( "Password is incorrect" );
            }

            Session session = null;
            try {
                session = Core.getInstance().getSessionManager().createSession( user, new Date(), 10 * 60 *60 );
            } catch( ItemInstantiationException e ) {
                throw new AuthenticationException( e );
            }
            return session;
        } else {
            throw new AuthenticationException( "" );
        }
    }

}
