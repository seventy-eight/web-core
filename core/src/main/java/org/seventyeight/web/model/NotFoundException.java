package org.seventyeight.web.model;

import org.seventyeight.web.CoreException;

/**
 * @author cwolfgang
 */
public class NotFoundException extends CoreException {

    public NotFoundException( String m ) {
        super( m );

        this.code = 404;
        this.header = "Page not found";
    }

    public NotFoundException( String m, String header ) {
        super( m );

        this.code = 404;
        this.header = header;
    }

    public NotFoundException( String m, String header, Exception e ) {
        super( m, e );

        this.code = 404;
        this.header = header;
    }
}
