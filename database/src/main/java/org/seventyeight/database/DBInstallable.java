package org.seventyeight.database;

/**
 * @author cwolfgang
 */
public interface DBInstallable {

    public void install() throws DatabaseException;

    public boolean isInstalled() throws DatabaseException;
}
