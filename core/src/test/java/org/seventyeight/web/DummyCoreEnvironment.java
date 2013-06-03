package org.seventyeight.web;

import java.io.File;

/**
 * @author cwolfgang
 */
public class DummyCoreEnvironment extends WebCoreEnv<Core> {

    public DummyCoreEnvironment( String databaseName ) {
        super( databaseName );
    }

    @Override
    public Core getCore( File path, String databaseName ) throws CoreException {
        return new DummyCore( path, databaseName );
    }
}
