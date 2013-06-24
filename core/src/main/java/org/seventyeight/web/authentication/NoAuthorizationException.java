package org.seventyeight.web.authentication;

import org.seventyeight.web.CoreException;

/**
 * @author cwolfgang
 */
public class NoAuthorizationException extends CoreException {
    public NoAuthorizationException( String m ) {
        super( m );
    }

    public NoAuthorizationException( Exception e ) {
        super( e );
    }
}
