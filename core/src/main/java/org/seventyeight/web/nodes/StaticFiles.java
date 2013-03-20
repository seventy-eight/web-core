package org.seventyeight.web.nodes;

import org.apache.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.Autonomous;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * @author cwolfgang
 */
public class StaticFiles implements Action, Autonomous {

    private static Logger logger = Logger.getLogger( StaticFiles.class );

    @Override
    public Node getChild( String name ) throws NotFoundException {
        return null;
    }

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public String getUrlName() {
        return "static";
    }

    @Override
    public String getDisplayName() {
        return "Static files";
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
            file = Core.getInstance().getTemplateManager().getStaticFile( filename );
            response.deliverFile( request, file, true );
        } catch( IOException e ) {
            try {
                Response.NOT_FOUND_404.render( request, response );
            } catch( TemplateException e1 ) {
                throw new IOException( e );
            }
        }
    }
}