package org.seventyeight.web.actions;

import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;

/**
 * @author cwolfgang
 */
public class NewContent implements Action {

    private static Logger logger = Logger.getLogger( NewContent.class );

    @Override
    public String getUrlName() {
        return "new";
    }

    @Override
    public String getName() {
        return "New content";
    }

    public void doCreate( Request request, Response response, JsonObject jsonData ) throws IOException {
        String className = request.getValue( "className" );

        if( className == null ) {
            throw new IOException( "No className given" );
        }

        /* Get the resource descriptor from the className name */
        NodeDescriptor<?> descriptor = null;
        try {
            descriptor = (NodeDescriptor<?>) Core.getInstance().getDescriptor( className );
        } catch( ClassNotFoundException e ) {
            throw new IOException( e );
        }

        if( descriptor == null ) {
            throw new IOException( new MissingDescriptorException( "Could not find descriptor for " + className ) );
        }

        /* First of all we need to create the resource node */
        logger.debug( "Newing resource" );
        AbstractNodeItem r = null;
        try {
            r = descriptor.newInstance();
            String title = request.getValue( "title", "" );
            r.setTitle( title );
        } catch( ItemInstantiationException e ) {
            throw new IOException( e );
        }
        logger.debug( "RESOURCE IS " + r );

        r.setOwner( request.getUser() );
        r.save();

        response.sendRedirect( r.getUrl() + "configure" );
        //ResourceUtils.getConfigureResourceView( request, response, r, descriptor );
    }
}
