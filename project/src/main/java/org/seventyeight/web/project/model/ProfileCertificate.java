package org.seventyeight.web.project.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.authentication.NoAuthorizationException;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cwolfgang
 */
public class ProfileCertificate implements Node {

    private static Logger logger  = Logger.getLogger( ProfileCertificate.class );

    private Node parent;
    private Certificate certificate;
    private MongoDocument document;
    private List<Validation> validations;

    public ProfileCertificate( Node parent, Certificate certificate, MongoDocument document ) {
        this.certificate = certificate;
        this.parent = parent;
        this.document = document;


    }

    private void extractValidations() throws NotFoundException, ItemInstantiationException {
        List<MongoDocument> docs = document.getList( "validatedby" );
        validations = new ArrayList<Validation>( docs.size() );

        for( MongoDocument d : docs ) {
            Profile p = Core.getInstance().getNodeById( this, (String) d.get( "profile" ) );
            validations.add( new Validation( (Date) d.get( "date" ), p ) );
        }
    }

    public List<Validation> getValidations() throws NotFoundException, ItemInstantiationException {
        if( validations == null ) {
            extractValidations();
        }

        return validations;
    }

    public int getNumberOfValidations() {
        try {
            return getValidations().size();
        } catch( Exception e ) {
            return 0;
        }
    }

    public Validation getValidation( int i ) throws NotFoundException, ItemInstantiationException {
        return getValidations().get( i );
    }

    public void doGetValidations( Request request, Response response ) throws IOException {
        int number = request.getValue( "number", 10 );
        int offset = request.getValue( "offset", 0 );

        List<MongoDocument> docs = document.getList( "validatedby" );

        PrintWriter writer = response.getWriter();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        writer.write( gson.toJson( docs ) );
    }

    public void doGetValidations2( Request request, Response response ) throws IOException, NotFoundException, ItemInstantiationException, TemplateException {
        int number = request.getValue( "number", 10 );
        int offset = request.getValue( "offset", 0 );

        List<String> strings = new ArrayList<String>( number );

        List<MongoDocument> docs = document.getList( "validatedby" );

        for( MongoDocument d : docs ) {
            String pid = d.get( "profile" );
            Profile p = Core.getInstance().getNodeById( this, pid );
            strings.add( Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( p, "small.vm" ) );
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

    public List<MongoDocument> getValidationDocuments() {
        return document.getList( "validatedby" );
    }

    public Validation getLastValidation() throws ItemInstantiationException, NotFoundException {
        if( getValidationDocuments() != null && getValidationDocuments().size() > 0 ) {
            MongoDocument d = getValidationDocuments().get( getValidationDocuments().size() - 1 );
            Profile validator = (Profile) Core.getInstance().getNodeById( this, (String) d.get( "profile" ) );
            return new Validation( (Date) d.get("date"), validator );
        } else {
            return null;
        }
    }

    public void doValidate( Request request, Response response ) throws NoAuthorizationException {
        //Profile profile = (Profile) parent.getParent();
        Profile user = (Profile) request.getUser();

        request.setResponseType( Request.ResponseType.HTTP_CODE );
        //request.checkAuthorization( (Authorizer) pr, Authorizer.Authorization.MODERATE );
        if( request.isAuthenticated() ) {
            validateCertificate( user );
        } else {
            throw new NoAuthorizationException( "You are not authenticated" );
        }
    }

    public void validateCertificate( Profile profile ) {
        logger.debug( "Validating certificate " + certificate + " for " + this + " by " + profile );
        MongoDocument d = new MongoDocument(  ).set( "profile", profile.getIdentifier() ).set( "date", new org.seventyeight.utils.Date() );
        document.addToList( "validatedby", d );
        //((Profile)parent.getParent().getParent()).save();
        Core.superSave( this );
    }

    public Profile getProfile() {
        return Core.superGet( parent );
    }

    public Certificate getCertificate() {
        return certificate;
    }

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
    }
}
