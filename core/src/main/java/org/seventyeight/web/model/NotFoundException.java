package org.seventyeight.web.model;

import org.seventyeight.web.CoreException;

/**
 * @author cwolfgang
 */
public class NotFoundException extends CoreException {

    public NotFoundException( String m ) {
        super( m );
    }
}
