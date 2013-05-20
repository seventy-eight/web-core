package org.seventyeight.web.project.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Entity;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NodeDescriptor;
import org.seventyeight.web.model.NotFoundException;

/**
 * @author cwolfgang
 */
public class Certificate extends Entity<Certificate> {

    public static final String CERTIFICATE_STRING = "certificate";
    public static final String CERTIFICATE_STRING_PL = "certificates";
    public static final String CERTIFICATE_NAME = "Certificate";

    public Certificate( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String getDisplayName() {
        return CERTIFICATE_NAME;
    }

    @Override
    public String getMainTemplate() {
        return Core.MAIN_TEMPLATE;
    }

    @Override
    public String getPortrait() {
        return null;
    }

    public static class CertificateDescriptor extends NodeDescriptor<Certificate> {

        @Override
        public String getType() {
            return CERTIFICATE_STRING;
        }

        @Override
        public String getDisplayName() {
            return CERTIFICATE_NAME;
        }

        @Override
        public Node getChild( String name ) throws NotFoundException {
            return null;
        }
    }
}
