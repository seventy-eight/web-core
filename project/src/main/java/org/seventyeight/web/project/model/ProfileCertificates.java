package org.seventyeight.web.project.model;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.Date;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.authentication.NoAuthorizationException;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author cwolfgang
 */
public class ProfileCertificates extends Action<ProfileCertificates> implements Getable<ProfileCertificate> {

    private static Logger logger = Logger.getLogger( ProfileCertificates.class );

    public ProfileCertificates( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public String getDisplayName() {
        return "Profile certificates";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    public boolean hasCertificate( String certificateId ) throws NotFoundException {
        logger.debug( "Does " + this + " have " + certificateId );
        //Certificate c = Certificate.getCertificateByTitle( certificateName, this );
        //List<MongoDocument> docs = MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).findOne( new MongoDBQuery().is( Certificate.CERTIFICATES + "." +  ) )

        /*
        List<MongoDocument> docs = document.getList( Certificate.CERTIFICATES );

        logger.debug( "DOCS: " + docs );

        for( MongoDocument d : docs ) {
            if( d.get( Certificate.CERTIFICATE, "" ).equals( certificateId ) ) {
                return true;
            }
        }
        */

        logger.debug( "SUB DOC: " + document );
        MongoDocument d = null;
        try {
            d = ((MongoDocument)document.get( Certificate.CERTIFICATES )).getSubDocument( certificateId, null );
        } catch( Exception e ) {
            return false;
        }


        return ( d != null && !d.isNull() );
    }

    @PostMethod
    public void doAdd( Request request, Response response ) throws ItemInstantiationException, IOException, TemplateException, NoAuthorizationException {
        request.setResponseType( Request.ResponseType.HTTP_CODE );
        request.checkAuthorization( (Authorizer) parent, Authorizer.Authorization.MODERATE );

        String title = request.getValue( "certificateTitle", null );
        logger.debug( "Adding " + title + " to " + this );

        if( title != null ) {
            Certificate c = null;
            try {
                c = Certificate.getCertificateByTitle( title, this );

                if( hasCertificate( c.getIdentifier() ) ) {
                    //Response.NOT_ACCEPTABLE.render( request, response, "The certificate " + title + " is already possessed by " + this );
                    response.sendError( Response.SC_NOT_ACCEPTABLE, this + " already have " + c );
                    return;
                }
            } catch( NotFoundException e ) {
                logger.debug( e );

                c = Certificate.createCertificate( title );
                c.save();
            }

            addCertificate( c );
        } else {
            logger.debug( "No certificate title given" );
        }

    }

    @Override
    public ProfileCertificate get( String token ) throws NotFoundException {
        logger.debug( "Get certificate " + token );

        /*
        List<MongoDocument> docs = document.getList( Certificate.CERTIFICATES );

        MongoDBQuery q1 = new MongoDBQuery().is( ((ExtensionDescriptor)getDescriptor()).getMongoPath() + "certificates.certificate", token );
        MongoDocument fields = new MongoDocument(  ).set( "_id", 0 ).set( ((ExtensionDescriptor)getDescriptor()).getMongoPath() + "certificates.$", 1 );
        MongoDocument d = MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).findOne( q1, fields );
        logger.debug( d );
        */

        MongoDocument d = ((MongoDocument)document.get( Certificate.CERTIFICATES )).getSubDocument( token, null );

        if( d != null && !d.isNull() ) {
            logger.debug( "Found the id in the list" );
            try {
                Certificate c = (Certificate) Core.getInstance().getNodeById( this, token );
                return new ProfileCertificate( this, c, d );
            } catch( ItemInstantiationException e ) {
                throw new NotFoundException( e.getMessage(), "Error while finding", e );
            }

        } else {
            logger.debug( "D was null" );
        }

        throw new NotFoundException( parent + " does not have the certificate " + token );
    }


    public List<ProfileCertificate> getCertificates( int offset, int number ) throws ItemInstantiationException, NotFoundException {
        //List<MongoDocument> docs = MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).find( new MongoDBQuery().is( "type", Certificate.CERTIFICATE ), offset, number );
        MongoDocument docs = document.get( Certificate.CERTIFICATES );

        if( docs != null && !docs.isNull() ) {

            Map map = docs.getMap();
            List<ProfileCertificate> certificates = new ArrayList<ProfileCertificate>( map.size() );

            for( Object key : map.keySet() ) {
                Certificate c = (Certificate) Core.getInstance().getNodeById( this, (String) key );

                //logger.debug( "------> " + map.get( key ) );
                ProfileCertificate pc = new ProfileCertificate( this, c, new MongoDocument( (BasicDBObject) map.get( key ) ) );
                //List<MongoDocument> sd = d.getList( "validatedby" );

                //pc.setValidations( sd );

                certificates.add( pc );
            }

            return certificates;
        } else {
            return Collections.emptyList();
        }
    }

    public void doList( Request request, Response response ) throws IOException, TemplateException {
        PrintWriter writer = response.getWriter();
        writer.write( ( Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( this, "list.vm" ) ) );
    }

    public void addCertificate( Certificate certificate ) {
        logger.debug( "Adding certificate " + certificate );

        MongoDocument certs = document.get( Certificate.CERTIFICATES );

        if( certs != null && !certs.isNull() ) {
            certs.set( certificate.getIdentifier(), new MongoDocument().set( "added", new Date() ) );
        } else {
            MongoDocument d = new MongoDocument().set( certificate.getIdentifier(), new MongoDocument().set( "added", new Date() ) );
            document.set( Certificate.CERTIFICATES, d );
        }

        ((Profile)parent).save();
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static class ProfileCertificateDescriptor extends Action.ActionDescriptor<ProfileCertificates> {

        @Override
        public String getDisplayName() {
            return "Profile certificate";
        }

        @Override
        public String getExtensionName() {
            return "certificates";
        }

        /*
        @Override
        public String getUrlName() {
            return "certificate";
        }
        */

        @Override
        public boolean isApplicable( Node node ) {
            return node instanceof Profile;
        }
    }
}
