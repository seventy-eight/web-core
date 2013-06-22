package org.seventyeight.web.actions;

import org.apache.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;

/**
 * @author cwolfgang
 */
public class GlobalConfiguration implements Node, Parent {

    private static Logger logger = Logger.getLogger( GlobalConfiguration.class );

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public Node getChild( String name ) throws NotFoundException {
        logger.debug( "Configuration for " + name );

        try {
            Descriptor d = Core.getInstance().getDescriptor( name );
            return new ViewWrapper( (Node)d, "globalConfigure" ).setClassOffset( d.getClazz() ).setPostViewTemplate( "submit" );
        } catch( ClassNotFoundException e ) {
            logger.debug( e );
            return null;
        }
    }

    public void doGlobal( Request request, Response response ) throws IOException {
        response.getWriter().println( "BOOOOOOM!" );
    }

    @Override
    public String getDisplayName() {
        return "Global configuration";
    }

    @Override
    public String getMainTemplate() {
        return Core.MAIN_TEMPLATE;
    }
}
