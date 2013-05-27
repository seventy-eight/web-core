package org.seventyeight.web;

import org.apache.log4j.Logger;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.project.install.*;
import org.seventyeight.web.project.model.Profile;
import org.seventyeight.web.project.model.Role;

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

        WolfgangInstall wolfgang = new WolfgangInstall();
        if( wolfgang.isInstalled() ) {
            logger.debug( "wolfgang was already installed." );
        } else {
            logger.debug( "Installing wolfgang" );
            wolfgang.install();
        }

        AnonymousInstall ani = new AnonymousInstall();
        if( ani.isInstalled() ) {
            logger.debug( "Anonymous was already installed." );
        } else {
            logger.debug( "Installing Anonymous" );
            ani.install();
        }

        Core.getInstance().setAnonymous( ani.getUser() );


        Profile admin = null;
        try {
            admin = Profile.getProfileByUsername( AdminInstall.ADMIN_NAME, Core.getInstance() );
        } catch( NotFoundException e ) {
            e.printStackTrace();
        }



        AdminRoleInstall ari = new AdminRoleInstall( admin );
        if( ari.isInstalled() ) {
            logger.debug( "Admin role already installed." );
        } else {
            logger.debug( "Installing admin role" );
            ari.install();
        }
        Role adminRole = ari.getRole();
        wolfgang.getProfile().addGroup( adminRole );

        CertificateInstall tci = new CertificateInstall( admin );
        if( tci.isInstalled() ) {
            logger.debug( "Test cert was already installed." );
        } else {
            logger.debug( "Installing test cert" );
            tci.install();
        }
    }

    @Override
    public ProjectCore getCore( File path, String dbname ) throws CoreException {
        return new ProjectCore( path, dbname );
    }
}
