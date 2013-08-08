package org.seventyeight.web.project.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;
import org.seventyeight.utils.Date;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.authentication.NoAuthorizationException;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.*;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Profile extends User {

    private static Logger logger = Logger.getLogger( Profile.class );

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";

    public Profile( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public Saver getSaver( CoreRequest request ) {
        return new ProfileSaver( this, request );
    }

    public class ProfileSaver extends UserSaver {
        public ProfileSaver( AbstractNode modelObject, CoreRequest request ) {
            super( modelObject, request );
        }

        @Override
        public void save() throws SavingException {
            super.save();

            logger.debug( "Saving profile" );

            set( FIRST_NAME );
            set( LAST_NAME );
        }
    }

    public static Profile getProfileByUsername( Node parent, String username ) {
        List<MongoDocument> docs = MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).find( new MongoDBQuery().is( "username", username ), 0, 1 );

        if( docs != null && !docs.isEmpty() ) {
            return new Profile( parent, docs.get( 0 ) );
        } else {
            logger.debug( "The user " + username + " was not found" );
            return null;
        }
    }

    /*
    @Override
    public Node getChild( String name ) {
        if( name.equalsIgnoreCase( "signature" ) ) {
            MongoDocument doc = document.get( "signature", new MongoDocument() );
            return new Signature( this, doc );
        } else {
            return super.getChild( name );
        }
    }
    */

    @Override
    public String toString() {
        return "Profile[" + getDisplayName() + "]";
    }

    public void doListCertificates( Request request, Response response ) throws TemplateException {
        logger.debug( "DO LIST BEFORE" );
        Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( this, "listCertificates.vm" );
        logger.debug( "DO LIST AFTER" );
    }

    @Override
    public String getDisplayName() {
        return document.get( FIRST_NAME ) + " " + document.get( LAST_NAME );
    }

    public static Profile getProfileByUsername( String username, Node parent ) throws NotFoundException {
        MongoDocument doc = User.getUserDocumentByUsername( username );
        if( doc != null ) {
            return new Profile( parent, doc );
        } else {
            throw new NotFoundException( "The profile " + username + " was not found", "Profile not found" );
            //throw new NotFoundException( "" ).setHeader( "" );
        }
    }

    public void addRole( Role role ) {
        role.addMember( this );
    }

    public void getLastValidation() {

    }

    public void validateCertificate( Certificate certificate, Profile profile ) {
        logger.debug( "Validating certificate " + certificate + " for " + this + " by " + profile );

        MongoDocument d = new MongoDocument().set( "profile", profile.getIdentifier() ).set( "date", new Date() );

        MongoDBQuery q = new MongoDBQuery().is( Certificate.CERTIFICATE_DOTTED, certificate.getIdentifier() ).getId( this.getIdentifier() );
        //MongoUpdate u = new MongoUpdate().set( Certificate.CERTIFICATES + ".$.validatedby", d );
        MongoUpdate u = new MongoUpdate().push( Certificate.CERTIFICATES + ".$.validatedby", d );
        MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).update( q, u );
    }

    public static class ProfileDescriptor extends UserDescriptor {

        @Override
        public Node getChild( String name ) throws NotFoundException {
            return getProfileByUsername( name, this );
        }

        @Override
        public String getDisplayName() {
            return "Profile";
        }

        @Override
        public String getType() {
            return "profile";
        }

        @Override
        public void save( Request request, Response response ) {
            /**/
        }
    }
}
