package org.seventyeight.web;

import org.seventyeight.web.nodes.Group;
import org.seventyeight.web.nodes.User;

import java.io.File;

/**
 * @author cwolfgang
 */
public class DummyCore extends Core {
    public DummyCore( File path, String dbname ) throws CoreException {
        super( path, dbname );

        addDescriptor( new Group.GroupDescriptor() );
        addDescriptor( new User.UserDescriptor() );
    }
}
