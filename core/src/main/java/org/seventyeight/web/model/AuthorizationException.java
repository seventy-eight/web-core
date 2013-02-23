package org.seventyeight.web.model;

import org.seventyeight.web.CoreException;

/**
 * @author cwolfgang
 *         Date: 23-02-13
 *         Time: 12:22
 */
public class AuthorizationException extends CoreException {

    public AuthorizationException( Exception e ) {
        super( e );
    }
}
