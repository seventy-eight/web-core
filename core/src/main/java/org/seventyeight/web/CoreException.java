package org.seventyeight.web;

/**
 * @author cwolfgang
 *         Date: 16-02-13
 *         Time: 23:25
 */
public class CoreException extends Exception {

    public CoreException( String m ) {
        super( m );
    }

    public CoreException( String m, Exception e ) {
        super( m, e );
    }
}
