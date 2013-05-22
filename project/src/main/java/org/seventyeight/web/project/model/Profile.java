package org.seventyeight.web.project.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Profile extends User {

    private static Logger logger = Logger.getLogger( Profile.class );

    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String LAST_NAME = "LAST_NAME";

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

    @Override
    public String toString() {
        return "Profile[" + getDisplayName() + "]";
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
            throw new NotFoundException( "The profile " + username + " was not found" );
        }
    }

    public List<Certificate> getCertificates( int offset, int number ) {
        List<MongoDocument> docs = MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).find( new MongoDBQuery().is( "type", Certificate.CERTIFICATE_STRING ), offset, number );

        List<Certificate> certificates = new ArrayList<Certificate>( docs.size() );

        for( MongoDocument d : docs ) {
            certificates.add( new Certificate( this, d ) );
        }

        return certificates;
    }

    public void addCertificate( Certificate certificate ) {
        MongoDocument d = new MongoDocument().set( Certificate.CERTIFICATE_STRING, certificate.getIdentifier() );
        document.addToList( Certificate.CERTIFICATE_STRING_PL, d );
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
