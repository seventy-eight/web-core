package org.seventyeight.web.velocity.html;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.servlet.Request;

import java.io.IOException;
import java.io.Writer;

public class RenderObject extends Directive {

	private Logger logger = Logger.getLogger( RenderObject.class );
	
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
        int superClass = 0;
        Class<?> clazz2 = null;

		try {
			if( node.jjtGetChild( 0 ) != null ) {
				obj = (Object) node.jjtGetChild( 0 ).value( context );
			} else {
				throw new IOException( "First argument is not an object" );
			}

            if( node.jjtGetChild( 1 ) != null ) {
                template = (String) node.jjtGetChild( 1 ).value( context );
            } else {
                throw new IOException( "Second argument is not a string" );
            }

            if( node.jjtGetChild( 2 ) != null ) {
                superClass = (Integer) node.jjtGetChild( 2 ).value( context );
            } else {
                throw new IOException( "Third argument is not a string" );
            }

            if( node.jjtGetChild( 3 ) != null ) {
                clazz2 = (Class<?>) node.jjtGetChild( 3 ).value( context );
            } else {
                throw new IOException( "Fourth argument is not a class" );
            }

		} catch( Exception e ) {
            logger.debug( e );
		}

        Request request = (Request) context.get( "request" );

        if( template == null ) {
            return false;
        } else {
            /* Default behaviour */
            if( superClass == 0 && clazz2 == null ) {
                try {
                    writer.write( Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( obj, template + ".vm", false ) );
                } catch( TemplateException e ) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Class<?> clazz = clazz2 != null ? clazz2 : obj.getClass();
                    int c = 0;
                    logger.debug("BASE IS " + clazz);
                    while( clazz != null && clazz != Object.class && c < superClass ) {
                        clazz = clazz.getSuperclass();
                        logger.debug("SUPER IS " + clazz);
                        c++;
                    }
                    logger.debug("USING " + clazz);
                    writer.write( (Core.getInstance().getTemplateManager().getRenderer( request ).renderClass( obj, clazz, template + ".vm", false ) ) );
                } catch( NotFoundException e ) {
                    e.printStackTrace();
                }
            }
        }

        return true;
	}

}
