package org.seventyeight.web.project;

import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.CoreException;
import org.seventyeight.web.ProjectCore;
import org.seventyeight.web.WebCoreEnv;
import org.seventyeight.web.project.install.CertificateInstall;
import org.seventyeight.web.project.install.ProfileInstall;
import org.seventyeight.web.project.model.Certificate;
import org.seventyeight.web.project.model.Profile;

import java.io.File;

/**
 * @author cwolfgang
 */
public class ProjectEnvironment extends WebCoreEnv<ProjectCore> {

    public ProjectEnvironment( String databaseName ) {
        super( databaseName );
    }

    @Override
    public ProjectCore getCore( File path, String databaseName ) throws CoreException {
        return new ProjectCore( path, databaseName );
    }

    public Profile createProfile( String name ) throws DatabaseException {
        ProfileInstall ui = new ProfileInstall( name, "First", "Last", name + "@seventyeight.org" );
        ui.install();
        return (Profile) ui.getValue();
    }

    public Certificate createCertificate( String name, Profile owner ) throws DatabaseException {
        CertificateInstall i = new CertificateInstall( name, owner );
        i.install();
        return i.getValue();
    }

}
