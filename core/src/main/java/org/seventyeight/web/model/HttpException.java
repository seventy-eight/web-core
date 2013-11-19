package org.seventyeight.web.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.CoreException;

/**
 * @author cwolfgang
 */
public class HttpException extends CoreException {

    private static Logger logger = LogManager.getLogger( HttpException.class );

    private int code = 400;
    private String header = null;

    public HttpException( Exception e ) {
        this( e.getMessage(), e );
    }

    public HttpException( String m, Exception e ) {
        super( m, e );

        if( e instanceof ExceptionHeader ) {
            header = ( (ExceptionHeader) e ).getHeader();
            code = ( (ExceptionHeader) e ).getCode();
        }
    }

    public HttpException setCode( int code ) {
        this.code = code;
        return this;
    }

    public HttpException setHeader( String header ) {
        this.header = header;
        return this;
    }

    public int getCode() {
        return code;
    }

    public String getHeader() {
        return header;
    }

}
