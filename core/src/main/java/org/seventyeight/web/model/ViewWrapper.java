package org.seventyeight.web.model;

import org.apache.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.utilities.ExecuteUtils;

import java.io.IOException;

/**
 * @author cwolfgang
 */
public class ViewWrapper implements Node, Autonomous {

    private static Logger logger = Logger.getLogger( ViewWrapper.class );

    private Node node;
    private String view;
    private String postView;
    private Class<?> offset;

    public ViewWrapper( Node node, String template ) {
        this.node = node;
        this.view = template;
    }

    public ViewWrapper setPostViewTemplate( String template ) {
        this.postView = template;

        return this;
    }

    public ViewWrapper setClassOffset( Class<?> offset ) {
        this.offset = offset;

        return this;
    }

    public ViewWrapper setViewTemplate( String template ) {
        this.view = template;

        return this;
    }

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public Node getChild( String name ) throws NotFoundException {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "View wrapper for " + node;
    }

    @Override
    public String getMainTemplate() {
        return Core.MAIN_TEMPLATE;
    }

    @Override
    public void autonomize( Request request, Response response ) throws IOException {
        logger.debug( "View wrapping " + node + "(" + offset + ")" );

        String template;
        if( request.isRequestPost() && postView != null ) {
            template = postView;
        } else {
            template = view;
        }

        logger.debug( "TEMPLATE=" + template );

        try {
            ExecuteUtils.execute( request, response, node, template, offset );
            if( offset == null ) {
                //ExecuteUtils.execute( request, response, node, "index" );
                //request.getContext().put( "content", Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( node, template + ".vm" ) );
            } else {
                //ExecuteUtils.execute( request, response, node, "index", offset );
                //request.getContext().put( "content", Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( node, view + ".vm" ) );
                //request.getContext().put( "content", Core.getInstance().getTemplateManager().getRenderer( request ).renderClass( node, offset, template + ".vm" ) );
            }
            //response.getWriter().print( Core.getInstance().getTemplateManager().getRenderer( request ).render( request.getTemplate() ) );
        } catch( Exception e ) {
            throw new IOException( e );
        }
    }
}
