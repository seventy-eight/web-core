package org.seventyeight.web.authentication;

import org.seventyeight.web.CoreException;

public class AuthenticationException extends CoreException {

	public AuthenticationException( String s ) {
		super( s );
	}

    public AuthenticationException( Exception e ) {
        super( e );
    }
}
