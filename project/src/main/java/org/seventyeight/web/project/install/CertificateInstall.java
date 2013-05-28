package org.seventyeight.web.project.install;

import org.seventyeight.database.DBInstallable;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.Core;
import org.seventyeight.web.CoreException;
import org.seventyeight.web.installers.DefaultNodeInstall;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.project.model.Certificate;
import org.seventyeight.web.project.model.Profile;
import org.seventyeight.web.utilities.Parameters;

/**
 * @author cwolfgang
 */
public class CertificateInstall extends DefaultNodeInstall<Certificate> {

    public CertificateInstall( String title, User owner ) {
        super( title, owner );
    }

    @Override
    protected Descriptor<Certificate> getDescriptor() {
        return Core.getInstance().getDescriptor( Certificate.class );
    }
}
