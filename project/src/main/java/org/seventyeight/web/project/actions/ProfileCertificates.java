package org.seventyeight.web.project.actions;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.*;
import org.seventyeight.web.project.model.Certificate;
import org.seventyeight.web.project.model.Profile;
import org.seventyeight.web.project.model.ProfileCertificate;

/**
 * @author cwolfgang
 */
public class ProfileCertificates extends Action<ProfileCertificates> implements Parent {

    public ProfileCertificates( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public String getDisplayName() {
        return "Profile certificates";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    @Override
    public Node getChild( String name ) throws NotFoundException {
        Certificate c = Certificate.getCertificateByTitle( name, this );
        if( ((Profile)parent).hasCertificate( c.getIdentifier() ) ) {
            return new ProfileCertificate( this, (Profile) parent, c );
        } else {
            throw new NotFoundException( parent + " does not have " + name );
        }
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static class ProfileCertificateDescriptor extends Action.ActionDescriptor<ProfileCertificates> {

        @Override
        public String getDisplayName() {
            return "Profile certificate";
        }

        @Override
        public String getExtensionName() {
            return "certificate";
        }

        /*
        @Override
        public String getUrlName() {
            return "certificate";
        }
        */

        @Override
        public boolean isApplicable( Node node ) {
            return node instanceof Profile;
        }
    }
}
