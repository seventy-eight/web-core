package org.seventyeight.web.model;

import org.seventyeight.web.CoreException;

/**
 * @author cwolfgang
 *         Date: 16-02-13
 *         Time: 23:26
 */
public class ItemInstantiationException extends CoreException {

    public ItemInstantiationException( String m ) {
        super( m );
    }

    public ItemInstantiationException( String m, Exception e ) {
        super( m, e );
    }
}
