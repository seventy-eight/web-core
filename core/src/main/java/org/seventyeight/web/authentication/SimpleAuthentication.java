package org.seventyeight.web.authentication;

import org.apache.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import javax.servlet.http.Cookie;
import java.util.Date;


public class SimpleAuthentication implements Authentication {

	private static Logger logger = Logger.getLogger( SimpleAuthentication.class );
	
	public void authenticate( Request request, Response response ) throws AuthenticationException {

		String hash = null;
		
		for( Cookie cookie : request.getCookies() ) {
			logger.debug( "Cookie: " + cookie.getName() + "=" + cookie.getValue() );
			if( cookie.getName().equals( __SESSION_ID ) ) {
				hash = cookie.getValue();
				break;
			}
		}
		
		if( hash != null ) {
			logger.debug( "Found hash: " + hash );
			Session session = Core.getInstance().getSessionManager().getSession( hash );
			if( session != null ) {
				User user = session.getUser();
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
	}

    public Session login( String username, String password ) throws AuthenticationException {
        if( !username.isEmpty() ) {
            User user = User.getUserByUsername( null, username );

            if( user == null ) {
                throw new AuthenticationException( "The user " + username + " does not exist!" );
            }

            if( user.getPassword() != null && !password.equals( user.getPassword() ) ) {
                logger.debug( "Wrong password" );
                throw new AuthenticationException( "Passwords does not match" );
            }

            //request.initializeTransaction();
            Session session = null;
            try {
                session = Core.getInstance().getSessionManager().createSession( user, new Date(), 10 );
            } catch( ItemInstantiationException e ) {
                throw new AuthenticationException( e );
            }
            return session;
        } else {
            throw new AuthenticationException( "" );
        }
    }

}
