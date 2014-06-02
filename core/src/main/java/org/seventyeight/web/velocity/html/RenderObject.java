package org.seventyeight.web.velocity.html;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.handlers.template.TemplateManager;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.servlet.Request;

import java.io.IOException;
import java.io.Writer;

public class RenderObject extends Directive {

	private static Logger logger = LogManager.getLogger( RenderObject.class );
	
	@Override
	public String getName() {
		return "render";
	}

	@Override
	public int getType() {
		return LINE;
	}

	@Override
	public boolean render( InternalContextAdapter context, Writer writer, Node node ) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        logger.debug( "Rendering object" );
		Object obj = null;
        String template = null;
        boolean superClass = false;
        Class<?> clazz2 = null;
        Object injected = null;
        boolean newContext = false;

        if( node.jjtGetNumChildren() < 2 ) {
            throw new IOException( "Object and template must be set" );
        }

        obj = node.jjtGetChild( 0 ).value( context );
        template = (String) node.jjtGetChild( 1 ).value( context );

        if( node.jjtGetNumChildren() > 2 ) {
            superClass = (Boolean) node.jjtGetChild( 2 ).value( context );
        }

        if( node.jjtGetNumChildren() > 3 ) {
            try {
                clazz2 = (Class<?>) node.jjtGetChild( 3 ).value( context );
            } catch( ClassCastException e ) {
                String className = (String) node.jjtGetChild( 3 ).value( context );
                if( className.length() > 0 ) {
                    throw new IOException( "Fourth argument is not a class" );
                } // If not, "null"
            }
        }

        if( node.jjtGetNumChildren() > 4 ) {
            injected = node.jjtGetChild( 4 ).value( context );
        }

        if( node.jjtGetNumChildren() > 5 ) {
            newContext = (Boolean) node.jjtGetChild( 5 ).value( context );
        }

        Request request = (Request) context.get( "request" );

        logger.debug( "Rendering " + obj + " for " + template );

        if( template == null ) {
            return false;
        } else {
            TemplateManager.Renderer render = Core.getInstance().getTemplateManager().getRenderer( request );

            if( newContext ) {
                VelocityContext nc = new VelocityContext();
                nc.put( "request", request );
                render.setContext( nc );
            }

            if( injected != null ) {
                render.inject( "context", injected );
            }

            /* Default behaviour */
            if( clazz2 == null ) {
                try {
                    writer.write( render.renderObject( obj, template + ".vm", superClass ) );
                } catch( TemplateException e ) {
                    writer.write( "No " + template + " for " + obj );
                }
            } else {
                try {
                    logger.debug("USING " + clazz2);
                    writer.write( render.renderClass( obj, clazz2, template + ".vm", superClass ) );
                } catch( NotFoundException e ) {
                    writer.write( "No " + template + " for " + obj );
                }
            }
        }

        return true;
	}

}
