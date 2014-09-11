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

/**
 * Render object
 *
 * 0: The object
 * 1: The template, without .vm
 * 2: Boolean, use super class
 * 3: Use this class, if not null and not empty string
 * 4: Inject this object as $context
 * 5: Use new context, default false.
 */
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
        // 1
		Object obj = null;
		// 2
        String template = "view";
        // 3
        boolean superClass = false;
        // 4
        Class<?> clazz2 = null;
        // 5
        Object injected = null;
        // 6
        boolean newContext = false;

        if( node.jjtGetNumChildren() < 1 ) {
            throw new IOException( "Object must be set" );
        }

        // 1
        obj = node.jjtGetChild( 0 ).value( context );
        
        // 2
        if( node.jjtGetNumChildren() > 1 ) {
        	template = (String) node.jjtGetChild( 1 ).value( context );
        }

        // 3
        if( node.jjtGetNumChildren() > 2 ) {
            superClass = (Boolean) node.jjtGetChild( 2 ).value( context );
        }

        // 4
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

        // 5
        if( node.jjtGetNumChildren() > 4 ) {
            injected = node.jjtGetChild( 4 ).value( context );
        }

        // 6
        if( node.jjtGetNumChildren() > 5 ) {
            newContext = (Boolean) node.jjtGetChild( 5 ).value( context );
        }

        Request request = (Request) context.get( "request" );

        logger.debug( "Rendering " + obj + " for " + template );

        if( template == null ) {
            return false;
        } else {
            Core core = request.getCore();
            TemplateManager.Renderer render = core.getTemplateManager().getRenderer( request );

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
