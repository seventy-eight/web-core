package org.seventyeight.web;

import org.seventyeight.web.DatabaseContextListener;

import javax.servlet.annotation.WebListener;
import java.io.File;

/**
 * @author cwolfgang
 */
@WebListener
public class ProjectListener extends DatabaseContextListener<ProjectCore> {
    @Override
    public ProjectCore getCore( File path, String dbname ) throws CoreException {
        return new ProjectCore( path, dbname );
    }
}
