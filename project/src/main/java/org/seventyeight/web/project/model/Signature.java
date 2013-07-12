package org.seventyeight.web.project.model;

import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.ProjectCore;
import org.seventyeight.web.actions.AbstractUploadAction;
import org.seventyeight.web.extensions.NodeExtension;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * @author cwolfgang
 */
public class Signature extends AbstractUploadAction<Signature> implements Layoutable {

    private static Logger logger = Logger.getLogger( Signature.class );

    public Signature( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public File getPath() {
        return ((ProjectCore) Core.getInstance()).getSignaturePath();
    }

    public String getURL() {
        return ((ProjectCore) Core.getInstance()).getSignatureURL();
    }

    @Override
    public Authorizer.Authorization getUploadAuthorization() {
        return Authorizer.Authorization.MODERATE;
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        logger.debug( "HEHRHE" );
        document.set( "was", "here" );
    }

    @Override
    public String getUrlName() {
        return "signature";
    }

    @Override
    public String getDisplayName() {
        return "Signature";
    }

    public File getFileSignature() {
        return new File( new File( getPath(), ((Profile)parent).getIdentifier() ), "signature.jpg" );
    }

    public String getSignature() {
        return ((ProjectCore) Core.getInstance()).getSignatureURL() + ((Profile)parent).getIdentifier() + "/signature.jpg";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    public void doFetch( Request request, Response response ) throws IOException {
        String requestedFile = request.getPathInfo();

        requestedFile = requestedFile.replaceFirst( "^/?.*?/", "" );
        logger.debug( "[Request file] " + requestedFile );

        if( requestedFile == null ) {
            try {
                Response.NOT_FOUND_404.render( request, response );
            } catch( TemplateException e ) {
                throw new IOException( e );
            }
            return;
        }

        File file = null;
        try {
            file = getFileSignature();
            response.deliverFile( request, file, true );
        } catch( IOException e ) {
            try {
                Response.NOT_FOUND_404.render( request, response );
            } catch( TemplateException e1 ) {
                throw new IOException( e );
            }
        }
    }

    public static class SignatureDescriptor extends Action.ActionDescriptor<Signature> {

        @Override
        public String getDisplayName() {
            return "Signature";
        }

        @Override
        public String getExtensionName() {
            return "signature";
        }

        @Override
        public boolean isApplicable( Node node ) {
            return node instanceof Profile;
        }
    }

}
