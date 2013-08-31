package org.seventyeight.web.model;

/**
 * @author cwolfgang
 */
public interface Getable<T extends Object> {
    public T get( String token ) throws NotFoundException;
}
