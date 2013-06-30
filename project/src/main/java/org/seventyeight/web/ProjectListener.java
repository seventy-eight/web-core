package org.seventyeight.web;

import org.apache.log4j.Logger;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.installers.ArticleInstall;
import org.seventyeight.web.installers.GroupInstall;
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

        ProfileInstall cwInstall = new ProfileInstall( "cwolfgang", "Christian", "Wolfgang", "cwolfgang@seventyeight.org", "pass" );
        cwInstall.install();

        ProfileInstall anInstall = new ProfileInstall( "anonymous", "Anonymous", "Anonymous", "anonymous@seventyeight.org", "pass" );
        anInstall.install();

        GroupInstall agInstall = new GroupInstall( "Administrators", cwInstall.getValue() );
        agInstall.install();

        Core.getInstance().setAnonymous( anInstall.getValue() );
        cwInstall.getValue().addGroup( agInstall.getValue() );



        CertificateInstall cert1 = new CertificateInstall( "Test certificate", cwInstall.getValue() );
        cert1.install();

        CertificateInstall cert2 = new CertificateInstall( "Cookie jar", cwInstall.getValue() );
        cert2.install();

        CertificateInstall cert3 = new CertificateInstall( "Drivers license", cwInstall.getValue() );
        cert3.install();


        ArticleInstall ai1 = new ArticleInstall( "Article number 1", cwInstall.getValue() );
        ai1.install();

    }

    @Override
    public ProjectCore getCore( File path, String dbname ) throws CoreException {
        return new ProjectCore( path, dbname );
    }
}
