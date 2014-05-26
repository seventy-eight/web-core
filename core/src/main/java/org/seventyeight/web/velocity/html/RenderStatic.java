package org.seventyeight.web.velocity.html;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.servlet.Request;

import java.io.IOException;
import java.io.Writer;

public class RenderStatic extends Directive {

	private static Logger logger = LogManager.getLogger( RenderStatic.class );
	
	@Override
	public String getName() {
		return "renderStatic";
	}

	@Override
	public int getType() {
		return LINE;
	}

	@Override
	public boolean render( InternalContextAdapter context, Writer writer, Node node ) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        logger.debug( "Rendering class" );
        Class<?> clazz = null;
        String template = null;

        if( node.jjtGetNumChildren() < 2 ) {
            throw new IOException( "Class and template must be set" );
        }

        if( node.jjtGetChild( 0 ) != null ) {
            clazz = (Class) node.jjtGetChild( 0 ).value( context );
        } else {
            throw new IOException( "First argument is not a class" );
        }

        if( node.jjtGetChild( 1 ) != null ) {
            template = (String) node.jjtGetChild( 1 ).value( context );
        } else {
            throw new IOException( "Second argument is not a string" );
        }

        Request request = (Request) context.get( "request" );

        try {
            writer.write( Core.getInstance().getTemplateManager().getRenderer( request ).renderClass( clazz, template + ".vm", false ) );
        } catch( NotFoundException e ) {
            e.printStackTrace();
        }

        return true;
	}

}
