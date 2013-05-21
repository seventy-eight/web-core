package org.seventyeight.web.project.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;
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

    public Profile( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String toString() {
        return "Profile[" + getDisplayName() + "]";
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
        document.addToList( Certificate.CERTIFICATE_STRING_PL, certificate.getIdentifier() );
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
