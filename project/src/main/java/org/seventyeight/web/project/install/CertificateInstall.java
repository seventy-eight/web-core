package org.seventyeight.web.project.install;

import org.seventyeight.database.DBInstallable;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.Core;
import org.seventyeight.web.CoreException;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.project.model.Certificate;
import org.seventyeight.web.project.model.Profile;
import org.seventyeight.web.utilities.Parameters;

/**
 * @author cwolfgang
 */
public class CertificateInstall implements DBInstallable<Certificate> {

    private Profile owner;
    private String name;

    private Certificate certificate;

    public CertificateInstall( String name, Profile owner ) {
        this.owner = owner;
        this.name = name;
    }

    @Override
    public void install() throws DatabaseException {
        try {
            Certificate cert = (Certificate) Core.getInstance().getDescriptor( Certificate.class ).newInstance( name );

            Parameters p = new Parameters();
            p.setUser( owner );

            cert.save( p, null );
            this.certificate = cert;
        } catch( CoreException e ) {
            throw new DatabaseException( "Unable to create certificate, " + e.getMessage(), e );
        } catch( ClassNotFoundException e ) {
            throw new DatabaseException( e );
        }
    }

    @Override
    public Certificate getValue() {
        return certificate;
    }

    @Override
    public boolean isInstalled() throws DatabaseException {
        Certificate cert = (Certificate) AbstractNode.getNodeByTitle( Core.getInstance(), "Test cert" );
        if( cert != null ) {
            this.certificate = cert;
            return true;
        } else {
            return false;
        }
    }
}
