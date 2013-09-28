package org.seventyeight.web.actions;

import org.apache.log4j.Logger;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;

/**
 * @author cwolfgang
 */
public class NewContent implements Node {

    private static Logger logger = Logger.getLogger( NewContent.class );

    private Node parent;

    public NewContent( Node parent ) {
        this.parent = parent;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public String getDisplayName() {
        return "New content";
    }

    @PostMethod
    public void doCreate( Request request, Response response ) throws IOException {
        String className = request.getValue( "className" );

        if( className == null ) {
            throw new IOException( "No className given" );
        }

        /* Get the resource descriptor from the className name */
        ResourceDescriptor<?> descriptor = null;
        try {
            descriptor = (ResourceDescriptor<?>) Core.getInstance().getDescriptor( className );
        } catch( ClassNotFoundException e ) {
            throw new IOException( e );
        }

        if( descriptor == null ) {
            throw new IOException( new MissingDescriptorException( "Could not find descriptor for " + className ) );
        }

        /* First of all we need to create the resource node */
        logger.debug( "Newing resource" );
        AbstractNode r = null;
        try {
            String title = request.getValue( "title", "" );
            logger.debug( "Title is " + title );
            r = (AbstractNode) descriptor.newInstance( title );
            r.save();
        } catch( ItemInstantiationException e ) {
            throw new IOException( e );
        }
        logger.debug( "RESOURCE IS " + r );

        r.setOwner( request.getUser() );
        r.save();

        response.sendRedirect( r.getUrl() + "configure" );
        //ResourceUtils.getConfigureResourceView( request, response, r, descriptor );
    }

    @Override
    public String getMainTemplate() {
        return null;
    }
}
