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
import org.seventyeight.web.servlet.Request;

import java.io.IOException;
import java.io.Writer;

public class PreviewDirective extends Directive {

	private static Logger logger = LogManager.getLogger( PreviewDirective.class );
	
	@Override
	public String getName() {
		return "preview";
	}

	@Override
	public int getType() {
		return LINE;
	}

	@Override
	public boolean render( InternalContextAdapter context, Writer writer, Node node ) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

		Object r = null;
		logger.debug( "HERE0" );
		try {
			if( node.jjtGetChild( 0 ) != null ) {
				r = (Object) node.jjtGetChild( 0 ).value( context );
			} else {
				throw new IllegalStateException( "Not a resource identifier" );
			}

            Request request = (Request) context.get( "request" );
            Core core = request.getCore();
            writer.write( core.getTemplateManager().getRenderer( request ).renderObject( r, "preview.vm" ) );
			
		} catch( Exception e ) {
			logger.debug( e );
			writer.write( "???" );
		}
		
		

		return true;
	}

}
