package org.seventyeight.web.model;

import org.seventyeight.web.Core;

/**
 * @author cwolfgang
 */
public interface Getable<T extends Object> {
    public T get( Core core, String token ) throws NotFoundException;
}
