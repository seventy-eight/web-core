package org.seventyeight.web;

/**
 * @author cwolfgang
 */
public class CoreException extends Exception {

    protected int code = 400;
    protected String header = "Bad request";

    public CoreException( String m ) {
        super( m );
    }

    public CoreException( Exception e ) {
        super( e );

        if( e instanceof CoreException ) {
            this.header = ( (CoreException) e ).getHeader();
            this.code = ( (CoreException) e ).getCode();
        }
    }

    public CoreException( String m, Exception e ) {
        super( m, e );
    }

    public CoreException( String m, String header, int code ) {
        super( m );

        this.header = header;
        this.code = code;
    }

    public CoreException( Exception e, String header, int code ) {
        super( e );

        this.header = header;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getHeader() {
        return header;
    }
}
