package org.seventyeight.web.project.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;
import org.seventyeight.web.Core;
import org.seventyeight.web.authentication.NoAuthorizationException;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 * {
 *     certificate: {
 *         added: date  // The date added ??
 *         profile: pid // Last validator
 *     }
 * }
 * </pre>
 *
 * @author cwolfgang
 */
public class ProfileCertificate implements Node {

    private static Logger logger = Logger.getLogger( ProfileCertificate.class );

    public static final String LAST_VALIDATION = "lastValidation";
    public static final String VALIDATIONS_COLLECTION = "certificateValidations";

    private Node parent;
    private Certificate certificate;
    private MongoDocument document;
    private List<Validation> validations;

    public ProfileCertificate( Node parent, Certificate certificate, MongoDocument document ) {
        this.certificate = certificate;
        this.parent = parent;
        this.document = document;
    }

    public static ProfileCertificate create( Profile profile, Certificate certificate ) {
        /*
        MongoDocument d = createDocument( profile.getIdentifier() );

        d.set( Certificate.CERTIFICATE, certificate.getIdentifier() );
        d.setList( VALIDATEDBY );

        ProfileCertificate e = new ProfileCertificate( profile, certificate, d );

        return e;
        */
        return null;
    }

    public int getNumberOfValidations() {
        try {
            return getValidationDocuments().size();
        } catch( Exception e ) {
            return 0;
        }
    }

    public void doGetValidations( Request request, Response response ) throws IOException {
        // int number = request.getValue( "number", 10 );
        // int offset = request.getValue( "offset", 0 );

        List<MongoDocument> docs = getValidationDocuments();

        PrintWriter writer = response.getWriter();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        writer.write( gson.toJson( docs ) );
    }

    public void doRemove( Request request, Response response ) throws NoAuthorizationException {
        request.setResponseType( Request.ResponseType.HTTP_CODE );
        request.checkAuthorization( (Authorizer) parent.getParent(), Authorizer.Authorization.MODERATE );

        remove();
        response.setStatus( HttpServletResponse.SC_OK );
    }

    public void remove() {
        logger.debug( "Removing certificate and validations" );
        /* Remove from profile */
        MongoDBQuery query = new MongoDBQuery().getId( getProfile().getIdentifier() );
        //MongoUpdate pull = new MongoUpdate().pull( "extensions.action.certificates.certificates.certificate", certificate.getIdentifier() );
        MongoUpdate pull = new MongoUpdate().pull( "extensions.action.certificates.certificates", "certificate", certificate.getIdentifier() );
        MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).update( query, pull );

        /* Remove validations */
        removeValidations();
    }

    public void doGetValidations2( Request request, Response response ) throws IOException, NotFoundException, ItemInstantiationException, TemplateException {
        int number = request.getValue( "number", 10 );
        int offset = request.getValue( "offset", 0 );

        List<String> strings = new ArrayList<String>( number );

        List<MongoDocument> docs = getValidationDocuments();

        for( MongoDocument d : docs ) {
            String pid = d.get( "profile" );
            Profile p = Core.getInstance().getNodeById( this, pid );
            strings.add( Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( p, "badge.vm" ) );
        }

        logger.debug( "_-----> " + strings );

        PrintWriter writer = response.getWriter();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        writer.write( gson.toJson( strings ) );
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public String getDisplayName() {
        return "Profile certificate";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    public MongoDocument getValidationData() {
        MongoDBQuery query = new MongoDBQuery().is( "profile", getProfile().getIdentifier() ).is( Certificate.CERTIFICATE, certificate.getIdentifier() );
        return MongoDBCollection.get( VALIDATIONS_COLLECTION ).findOne( query );
    }

    public List<MongoDocument> getValidationDocuments() {
        MongoDocument doc = getValidationData();

        if( doc != null && !doc.isNull() ) {
            return doc.getList( "validatedby" );
        } else {
            return Collections.emptyList();
        }
    }

    public Validation getLastValidation2() throws ItemInstantiationException, NotFoundException {
        String pid = document.get( "profile", null );
        Date date = document.get( "date", null );
        if( pid != null && date != null ) {
            Profile profile = Profile.getNodeById( this, pid );
            return new Validation( date, profile );
        } else {
            return null;
        }
        /*
        if( getValidationDocuments() != null && getValidationDocuments().size() > 0 ) {
            MongoDocument d = getValidationDocuments().get( getValidationDocuments().size() - 1 );
            Profile validator = (Profile) Core.getInstance().getNodeById( this, (String) d.get( "profile" ) );
            return new Validation( (Date) d.get("date"), validator );
        } else {
            return null;
        }
        */
    }

    public Validation getLastValidation() {
        MongoDocument d = getValidationData();
        if( d != null && !d.isNull() ) {
            List<MongoDocument> docs = d.getList( "validatedby" );
            if( docs.size() > 0 ) {
                MongoDocument v = docs.get( docs.size() - 1 );
                String pid = v.get( "profile", null );
                Date date = v.get( "date", null );
                Profile profile = Profile.getNodeById( this, pid );
                return new Validation( date, profile );
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void doValidate( Request request, Response response ) throws NoAuthorizationException, IOException {
        //Profile profile = (Profile) parent.getParent();
        Profile user = (Profile) request.getUser();

        request.setResponseType( Request.ResponseType.HTTP_CODE );
        //request.checkAuthorization( (Authorizer) pr, Authorizer.Authorization.MODERATE );
        if( request.isAuthenticated() ) {
            validateCertificate( user );
        } else {
            throw new NoAuthorizationException( "You are not authenticated" );
        }

        response.getWriter().write( "BUH YEAH" );
    }

    private void removeValidations() {
        MongoDBQuery query = new MongoDBQuery().is( "profile", getProfile().getIdentifier() ).is( Certificate.CERTIFICATE, certificate.getIdentifier() );
        MongoDBCollection.get( VALIDATIONS_COLLECTION ).remove( query );
    }

    /**
     *
     * @param profile The {@link Profile} validating the {@link Certificate}.
     */
    public void validateCertificate( Profile profile ) {
        logger.debug( "Validating certificate " + certificate + " for " + this + " by " + profile );

        /* Update profile, no */
        //document.set( "profile", profile.getIdentifier() );
        //document.set( "date", new Date() );
        //Core.superSave( this );

        /* Update data, add! */
        logger.debug( "Adding data to " + VALIDATIONS_COLLECTION );
        MongoDocument doc = getValidationData();
        if( doc == null || doc.isNull() ) {
            doc = Validation.createNode( getProfile(), certificate );
        }

        doc.addToList( "validatedby", Validation.create( profile ) );
        MongoDBCollection.get( VALIDATIONS_COLLECTION ).save( doc );

        /*
        MongoDocument d = new MongoDocument(  ).set( "profile", profile.getIdentifier() ).set( "date", new org.seventyeight.utils.Date() );
        logger.debug( "VALIDATED: " + d );
        document.addToList( VALIDATEDBY, d );
        save();
        */
        //((Profile)parent.getParent().getParent()).save();
        //Core.superSave( this );
    }

    public Profile getProfile() {
        return Core.superGet( parent );
    }

    public Certificate getCertificate() {
        return certificate;
    }

    /**
     * <pre>
     * {
     *     profile: pid,
     *     certificate: cid,
     *     validations : [
     *         {
     *             profile: pid,
     *             date: date
     *         } ...
     *     ]
     * }
     * </pre>
     */
    public static class Validation {
        private Date date;
        private Profile profile;

        public Validation( Date date, Profile profile ) {
            this.date = date;
            this.profile = profile;
        }

        public Date getDate() {
            return date;
        }

        public Profile getProfile() {
            return profile;
        }

        public static MongoDocument create( Profile profile ) {
            return new MongoDocument().set( "profile", profile.getIdentifier() ).set( "date", new Date() );
        }

        public static MongoDocument createNode( Profile profile, Certificate certificate ) {
            MongoDocument d = new MongoDocument().set( "profile", profile.getIdentifier() ).set( Certificate.CERTIFICATE, certificate.getIdentifier() );
            return d;
        }
    }
}
