package org.seventyeight.web.authentication;

import org.seventyeight.web.CoreException;

/**
 * @author cwolfgang
 */
public class NoAuthorizationException extends CoreException {

    public NoAuthorizationException( String m ) {
        super( m );

        code = 401;
        header = "Unauthorized";
    }

    public NoAuthorizationException( Exception e ) {
        super( e );

        code = 401;
        header = "Unauthorized";
    }
}
