package org.seventyeight.web.project.actions;

import org.apache.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.ProjectCore;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.Autonomous;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * @author cwolfgang
 */
public class SignatureFile implements Autonomous, Node {

    private static Logger logger = Logger.getLogger( SignatureFile.class );

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public String getDisplayName() {
        return "Signature files";
    }

    @Override
    public void autonomize( Request request, Response response ) throws IOException {
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

        String filename = URLDecoder.decode( requestedFile, "UTF-8" );

        File file = null;
        try {
            file = ((ProjectCore)Core.getInstance()).getSignaturePath();
            response.deliverFile( request, file, true );
        } catch( IOException e ) {
            try {
                Response.NOT_FOUND_404.render( request, response );
            } catch( TemplateException e1 ) {
                throw new IOException( e );
            }
        }
    }

    @Override
    public String getMainTemplate() {
        return "org/seventyeight/web/main.vm";
    }
}
