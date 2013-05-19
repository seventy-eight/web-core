package org.seventyeight.web;

import org.apache.log4j.Logger;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.DatabaseContextListener;
import org.seventyeight.web.project.install.AdminInstall;

import javax.servlet.annotation.WebListener;
import java.io.File;

/**
 * @author cwolfgang
 */
@WebListener
public class ProjectListener extends DatabaseContextListener<ProjectCore> {

    private static Logger logger = Logger.getLogger( ProjectListener.class );

    public ProjectListener() {
        extraTemplatePaths.add( "WEB-INF/classes/templates" );
    }

    @Override
    protected void install() throws DatabaseException {
        AdminInstall ai = new AdminInstall();
        if( ai.isInstalled() ) {
            logger.debug( "Admin was already installed." );
        } else {
            logger.debug( "Installing admin" );
            ai.install();
        }
    }

    @Override
    public ProjectCore getCore( File path, String dbname ) throws CoreException {
        return new ProjectCore( path, dbname );
    }
}
