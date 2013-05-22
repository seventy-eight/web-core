package org.seventyeight.web.project.install;

import org.seventyeight.database.DBInstallable;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.Core;
import org.seventyeight.web.CoreException;
import org.seventyeight.web.project.model.Certificate;
import org.seventyeight.web.utilities.Parameters;

/**
 * @author cwolfgang
 */
public class TestCertificateInstall implements DBInstallable {

    @Override
    public void install() throws DatabaseException {
        try {
            Certificate cert = (Certificate) Core.getInstance().getDescriptor( Certificate.class ).newInstance( "Test cert" );

            Parameters p = new Parameters();

            cert.save( p, null );
        } catch( CoreException e ) {
            throw new DatabaseException( "Unable to create certificate, " + e.getMessage(), e );
        } catch( ClassNotFoundException e ) {
            throw new DatabaseException( e );
        }
    }

    @Override
    public boolean isInstalled() throws DatabaseException {
        return false;
    }
}
