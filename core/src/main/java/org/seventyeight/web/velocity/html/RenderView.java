package org.seventyeight.web.velocity.html;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.Actionable;
import org.seventyeight.web.servlet.Request;

import java.io.IOException;
import java.io.Writer;

public class RenderView extends Directive {

	private Logger logger = Logger.getLogger( RenderView.class );
	
	@Override
	public String getName() {
		return "view";
	}

	@Override
	public int getType() {
		return LINE;
	}

	@Override
	public boolean render( InternalContextAdapter context, Writer writer, Node node ) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        logger.debug( "Rendering view" );
		Object obj = null;
        String view = null;

		try {
			if( node.jjtGetChild( 0 ) != null ) {
				obj = (Object) node.jjtGetChild( 0 ).value( context );
			} else {
				throw new IOException( "First argument is not an object" );
			}

            if( node.jjtGetChild( 1 ) != null ) {
                view = (String) node.jjtGetChild( 1 ).value( context );
            } else {
                throw new IOException( "Second argument is not a string" );
            }

		} catch( Exception e ) {
            logger.debug( e );
		}

        Request request = (Request) context.get( "request" );

        if( obj instanceof Actionable ) {
            for( Action a :  ((Actionable)obj).getActions() ) {
                try {
                    if( Core.getInstance().getTemplateManager().templateExists( request.getTheme(), a, view + ".vm" ) ) {
                        writer.write( Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( obj, view + ".vm", false ) );
                    }
                } catch( TemplateException e ) {
                    logger.debug( e );
                }
            }
        }

        return true;
	}

}
