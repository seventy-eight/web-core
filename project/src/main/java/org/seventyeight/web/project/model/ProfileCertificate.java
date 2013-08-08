package org.seventyeight.web.project.model;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.authentication.NoAuthorizationException;
import org.seventyeight.web.model.*;
import org.seventyeight.web.project.model.Certificate;
import org.seventyeight.web.project.model.Profile;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.util.Date;
import java.util.List;

/**
 * @author cwolfgang
 */
public class ProfileCertificate implements Node {

    private Node parent;
    private Profile profile;
    private Certificate certificate;
    private List<MongoDocument> validations;

    public ProfileCertificate( Node parent, Profile profile, Certificate certificate ) {
        this.certificate = certificate;
        this.profile = profile;
        this.parent = parent;
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

    public void setValidations( List<MongoDocument> validations ) {
        this.validations = validations;
    }

    public Validation getLastValidation() throws ItemInstantiationException {
        if( validations != null && validations.size() > 0 ) {
            MongoDocument d = validations.get( validations.size()-1 );
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
            profile.validateCertificate( certificate, user );
        } else {
            throw new NoAuthorizationException( "You are not authenticated" );
        }
    }

    public List<Validation> getValidations() {
        return null;
    }

    public Profile getProfile() {
        return profile;
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
