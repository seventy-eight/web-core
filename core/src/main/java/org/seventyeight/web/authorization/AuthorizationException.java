package org.seventyeight.web.authorization;

import org.seventyeight.web.CoreException;

/**
 * @author cwolfgang
 */
public class AuthorizationException extends CoreException {

    public AuthorizationException( Exception e ) {
        super( e );
    }
}
