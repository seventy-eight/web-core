package org.seventyeight.web.model;

import org.seventyeight.web.CoreException;

/**
 * @author cwolfgang
 */
public class NotFoundException extends CoreException implements ExceptionHeader {

    private String header = null;

    public NotFoundException( String m ) {
        super( m );
    }

    public NotFoundException setHeader( String header ) {
        this.header = header;

        return this;
    }

    public String getHeader() {
        return header;
    }

    @Override
    public int getCode() {
        return 404;
    }
}
