package org.seventyeight.web.nodes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.Core;
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
public class ThemeFiles implements Autonomous, Node {

    private static Logger logger = LogManager.getLogger( ThemeFiles.class );

    private Core core;

    public ThemeFiles( Core core ) {
        this.core = core;
    }

    @Override
    public Node getParent() {
        return core.getRoot();
    }

    @Override
    public String getDisplayName() {
        return "Theme files";
    }

    @Override
    public void autonomize( Request request, Response response ) throws IOException {
        String requestedFile = request.getPathInfo();
        response.setRenderType( Response.RenderType.NONE );

        requestedFile = requestedFile.replaceFirst( "^/?.*?/", "" );
        logger.debug( "[Request file] " + requestedFile );
        String filename = URLDecoder.decode( requestedFile, "UTF-8" );

        File themeFile = null;
        try {
            themeFile = core.getThemeFile( request.getTheme(), request.getPlatform(), filename );
        } catch ( IOException e ) {
            try {
                Response.NOT_FOUND_404.render( request, response );
            } catch( TemplateException e1 ) {
                throw new IOException( e1 );
            }
            return;
        }

        logger.debug( "THE THEME FILE IS " + themeFile );

        response.deliverFile( request, themeFile, true );


    }

    @Override
    public String getMainTemplate() {
        return null;
    }
}
