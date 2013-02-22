package org.seventyeight.web.handlers;

import org.seventyeight.web.CoreException;

/**
 * @author cwolfgang
 *         Date: 22-02-13
 *         Time: 22:17
 */
public class GizmoException extends CoreException {

    public GizmoException( String m ) {
        super( m );
    }

    public GizmoException( Exception e ) {
        super( e );
    }
}
