package org.seventyeight.web.project.model;

import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
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

    /**
     * Get a {@link Certificate} by its title
     */
    public static Certificate getCertificateByTitle( String title, Node parent ) throws NotFoundException {
        MongoDocument doc = MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).findOne( new MongoDBQuery().is( "title", title ) );
        if( doc != null ) {
            return new Certificate( parent, doc );
        } else {
            throw new NotFoundException( "The certificate \"" + title + "\" was not found" );
        }
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
