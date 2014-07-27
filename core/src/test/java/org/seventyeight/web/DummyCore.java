package org.seventyeight.web;

import org.seventyeight.web.model.RootNode;
import org.seventyeight.web.nodes.Group;
import org.seventyeight.web.nodes.User;

import java.io.File;

/**
 * @author cwolfgang
 */
public class DummyCore extends Core {
    public DummyCore( RootNode root, File path, String dbname ) throws CoreException {
        super( root, path, dbname );

        addDescriptor( new Group.GroupDescriptor(this) );
        addDescriptor( new User.UserDescriptor(this) );
    }
}
