package org.seventyeight.web;

import org.seventyeight.web.model.RootNode;

import java.io.File;

/**
 * @author cwolfgang
 */
public class DummyCoreEnvironment extends WebCoreEnv<Core> {

    public DummyCoreEnvironment( RootNode root, String databaseName ) {
        super( root, databaseName );
    }

    @Override
    public Core getCore( RootNode root, File path, String databaseName ) throws CoreException {
        return new DummyCore( root, path, databaseName );
    }
}
