package org.seventyeight.web.project.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Certificate extends Entity<Certificate> {

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
        return null;
    }

    public static Certificate createCertificate( String certName ) throws ItemInstantiationException {
        Certificate cert = (Certificate) Core.getInstance().getDescriptor( Certificate.class ).newInstance( certName );
        return cert;
    }

    public List<Profile> getProfiles() {
        logger.debug( "Getting members for " + this );

        MongoDBQuery q = new MongoDBQuery().is( CERTIFICATES + "." + CERTIFICATE, getIdentifier() );
        List<MongoDocument> docs = MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).find( q );

        logger.debug( "DOCS ARE: " + docs );

        List<Profile> users = new ArrayList<Profile>( docs.size() );

        for( MongoDocument d : docs ) {
            users.add( new Profile( this, d ) );
        }

        return users;
    }

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

    /**
     * Get a {@link Certificate} by its title
     */
    public static Certificate getCertificateByTitle( String title, Node parent ) throws NotFoundException {
        MongoDocument doc = MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).findOne( new MongoDBQuery().is( "title", title ).is( "type", CERTIFICATE ) );
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

        @Override
        public List<Searchable> getSearchables() {
            List<Searchable> ss = new ArrayList<Searchable>( 1 );

            ss.add( new VerifiedBy() );

            return ss;
        }

        private class CertificateId extends Searchable {

            @Override
            public Class<? extends Node> getClazz() {
                return Profile.class;
            }

            @Override
            public String getName() {
                return "Certificate id";
            }

            @Override
            public String getMethodName() {
                return "certificate-id";
            }

            @Override
            public void search( MongoDBQuery query, Operator operator, String term ) throws SearchException {
                switch( operator ) {
                    case EQUALS:
                        query.elemMatch( CERTIFICATES, (MongoDocument) new MongoDocument().set( CERTIFICATE, term ) );
                        break;

                    default:
                        throw new SearchException( "Unsupported operator, " + operator );
                }
            }
        }

        private class VerifiedBy extends Searchable {

            @Override
            public Class<? extends Node> getClazz() {
                return Profile.class;
            }

            @Override
            public String getName() {
                return "Verified by";
            }

            @Override
            public String getMethodName() {
                return "verified-by";
            }

            @Override
            public void search( MongoDBQuery query, Operator operator, String term ) {
                //query.is(  )
            }
        }
    }
}
