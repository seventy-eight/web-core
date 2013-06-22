package org.seventyeight.web.project.actions;

import org.apache.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.CoreException;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.project.model.Certificate;
import org.seventyeight.web.project.model.Profile;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;

/**
 * @author cwolfgang
 */
public class AddCertificate extends AddAction {

    private static Logger logger = Logger.getLogger( AddCertificate.class );

    public static final Response.HttpCode CERTIFICATE_NOT_VALID = new Response.HttpCode( 400, "Not a valid certificate", "$request.getRequestURI()" );
    public static final Response.HttpCode PROFILE_NOT_VALID = new Response.HttpCode( 400, "Not a valid profile", "$request.getRequestURI()" );

    public AddCertificate( Node parent ) {
        super( parent );
    }

    public void doIndex( Request request, Response response ) throws IOException, TemplateException, ItemInstantiationException {
        String certName = request.getValue( "certName", null );
        String profileName = request.getValue( "profile", "" );

        /* TODO some authorization checking */

        if( certName != null && !certName.isEmpty() ) {
            Profile profile = null;
            if( !profileName.isEmpty() ) {
                profile = Profile.getProfileByUsername( this, profileName );
            } else {
                profile = (Profile) request.getUser();
            }
            if( profile == null ) {
                logger.debug( "Not a valid user" );
                PROFILE_NOT_VALID.render( request, response, new CoreException( "Not a valid user" ) );
            } else {
                /* Check cert existence */
                Certificate cert = (Certificate) AbstractNode.getNodeByTitle( this, certName );
                if( cert != null ) {
                    if( cert instanceof Certificate ) {
                        profile.addCertificate( cert );
                    } else {
                        logger.debug( "Not a certificate" );
                        CERTIFICATE_NOT_VALID.render( request, response, new CoreException( "Not a certificate" ) );
                    }
                } else {
                    /* Create new */
                    Certificate newcert = Certificate.createCertificate( certName );
                    newcert.update( request.getUser() );
                    newcert.save();
                    profile.addCertificate( newcert );
                }
            }
        } else {
            logger.debug( "Certificate not provided" );
            CERTIFICATE_NOT_VALID.render( request, response, new CoreException( "Certificate not provided" ) );
        }
    }

    @Override
    public String getDisplayName() {
        return "Add certificate";
    }

    @Override
    public String getMainTemplate() {
        return Core.MAIN_TEMPLATE;
    }
}
