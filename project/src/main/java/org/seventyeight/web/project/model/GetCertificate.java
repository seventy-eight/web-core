package org.seventyeight.web.project.model;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class GetCertificate extends Action<GetCertificate> implements Parent {

    public GetCertificate( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String getDisplayName() {
        return "Get certificate";
    }

    @Override
    public String getMainTemplate() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Node getChild( String name ) throws NotFoundException {
        Certificate c = AbstractNode.getNodeById( this, name );
        if( c == null ) {
            c = Certificate.getCertificateByTitle( name, this );
        }
        if( ((ProfileCertificates)parent).hasCertificate( c.getIdentifier() ) ) {
            return new ProfileCertificate( this, (Profile) parent, c );
        } else {
            throw new NotFoundException( parent + " does not have " + name );
        }
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static class GetCertificateDescriptor extends Action.ActionDescriptor<GetCertificate> {

        @Override
        public String getDisplayName() {
            return "Get profile certificate";
        }

        @Override
        public String getExtensionName() {
            return "get";
        }

        @Override
        public boolean isApplicable( Node node ) {
            return node instanceof ProfileCertificates;
        }
    }
}
