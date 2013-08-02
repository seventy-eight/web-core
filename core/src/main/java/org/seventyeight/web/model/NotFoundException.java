package org.seventyeight.web.model;

import org.seventyeight.web.CoreException;

/**
 * @author cwolfgang
 */
public class NotFoundException extends CoreException {

    public NotFoundException( String m ) {
        super( m );

        this.code = 404;
        this.header = "Not found";
    }

    public NotFoundException( String m, String header ) {
        super( m );

        this.code = 404;
        this.header = header;
    }
}
