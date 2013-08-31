package org.seventyeight.database;

/**
 * @author cwolfgang
 */
public interface DBInstallable<T> {

    public void install() throws DatabaseException;

    public T getValue();
}
