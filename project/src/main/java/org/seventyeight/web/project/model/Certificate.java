package org.seventyeight.web.project.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Certificate extends Resource<Certificate> {

    private static Logger logger = Logger.getLogger( Certificate.class );

    public static final String CERTIFICATE = "certificate";
    public static final String CERTIFICATES = "certificates";
    public static final String CERTIFICATE_NAME = "Certificate";

    public static final String CERTIFICATE_DOTTED = CERTIFICATES + "." + CERTIFICATE;

    public Certificate( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String getDisplayName() {
        return getTitle();
    }

    @Override
    public String getMainTemplate() {
        return Core.MAIN_TEMPLATE;
    }

    @Override
    public String getPortrait() {
        return "/theme/framed-certificate-small.png";
    }

    public static Certificate createCertificate( String certName ) throws ItemInstantiationException {
        Certificate cert = (Certificate) Core.getInstance().getDescriptor( Certificate.class ).newInstance( certName );
        return cert;
    }

    public List<Profile> getProfiles() {
        logger.debug( "Getting members for " + this );

        MongoDBQuery q = new MongoDBQuery().is( "extensions.action.certificates.certificates." + CERTIFICATE, getIdentifier() );
        List<MongoDocument> docs = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).find( q );

        logger.debug( "DOCS ARE: " + docs );

        List<Profile> users = new ArrayList<Profile>( docs.size() );

        for( MongoDocument d : docs ) {
            users.add( new Profile( this, d ) );
        }

        return users;
    }

    public boolean hasCertificate( Profile profile ) {
        MongoDBQuery q = new MongoDBQuery().is( "extensions.action.certificates.certificates." + CERTIFICATE, getIdentifier() ).getId( profile.getIdentifier() );
        MongoDocument docs = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).findOne( q );

        return ( docs != null && !docs.isNull() );
    }

    /*
    public void doAddCertified( Request request, Response response ) throws IOException, TemplateException {
        String profileName = request.getValue( "profile", null );
        logger.debug( "Adding " + profileName + " as certified" );

        if( profileName != null ) {
            Profile profile = Profile.getProfileByUsername( this, profileName );
            if( profile != null ) {
                profile.addCertificate( this );
                response.sendRedirect( "" );
                return;
            } else {
                Response.HttpCode c = new Response.HttpCode( 404, "Profile not found", "The profile " + profileName + " was not found" );
                c.render( request, response );
            }
        } else {
            Response.HttpCode c = new Response.HttpCode( 404, "Not provided", "No profile provided" );
            c.render( request, response );
        }

    }
    */

    /**
     * Get a {@link Certificate} by its title
     */
    public static Certificate getCertificateByTitle( String title, Node parent ) throws NotFoundException {
        MongoDocument doc = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).findOne( new MongoDBQuery().is( "title", title ).is( "type", CERTIFICATE ) );
        if( !doc.isNull() ) {
            return new Certificate( parent, doc );
        } else {
            throw new NotFoundException( "The certificate \"" + title + "\" was not found" );
        }
    }

    public static class CertificateDescriptor extends NodeDescriptor<Certificate> {

        @Override
        public String getType() {
            return CERTIFICATE;
        }

        @Override
        public String getDisplayName() {
            return CERTIFICATE_NAME;
        }

        @Override
        public boolean allowIdenticalNaming() {
            return false;
        }

        @Override
        public Node getChild( String name ) throws NotFoundException {
            Certificate cert = getCertificateByTitle( name, this );
            if( cert != null ) {
                return cert;
            } else {
                throw new NotFoundException( "The certificate " + cert + " was not found" );
            }
        }


    }
}
