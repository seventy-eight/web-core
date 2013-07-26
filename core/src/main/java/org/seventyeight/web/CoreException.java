package org.seventyeight.web;

/**
 * @author cwolfgang
 */
public class CoreException extends Exception {

    public CoreException( String m ) {
        super( m );
    }

    public CoreException( Exception e ) {
        super( e );
    }

    public CoreException( String m, Exception e ) {
        super( m, e );
    }

}
