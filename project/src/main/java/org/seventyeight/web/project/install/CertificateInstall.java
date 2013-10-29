package org.seventyeight.web.project.install;

import org.seventyeight.web.Core;
import org.seventyeight.web.installers.DefaultNodeInstall;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.project.model.Skill;

/**
 * @author cwolfgang
 */
public class CertificateInstall extends DefaultNodeInstall<Skill> {

    public CertificateInstall( String title, User owner ) {
        super( title, owner );
    }

    @Override
    protected Descriptor<Skill> getDescriptor() {
        return Core.getInstance().getDescriptor( Skill.class );
    }
}
