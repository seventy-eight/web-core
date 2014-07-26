package org.seventyeight.web.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import javax.servlet.http.Cookie;
import java.io.IOException;

/**
 * @author cwolfgang
 */
public class LanguageAction implements Node {

    private Logger logger = LogManager.getLogger( LanguageAction.class );

    private Core core;

    public LanguageAction( Core core ) {
        this.core = core;
    }

    @Override
    public Node getParent() {
        return core.getRoot();
    }

    @Override
    public String getDisplayName() {
        return "Language";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    public void doSet( Request request, Response response ) throws IOException {
        response.setRenderType( Response.RenderType.NONE );

        String language = request.getValue( "l", "english" );
        logger.debug( "Changing language to {}.", language );

        Cookie c = new Cookie( "language", language );
        c.setMaxAge( 10000 );
        c.setPath( "/" );
        response.addCookie( c );

        String prev = request.getHeader("referer");
        logger.debug( "prev {}", prev );
        response.sendRedirect( prev );
    }
}
