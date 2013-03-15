package org.seventyeight.web.velocity.html;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.AbstractTheme;
import org.seventyeight.web.model.NodeItem;

public class ThemeSelectInputDirective extends Directive {

	private Logger logger = Logger.getLogger( ThemeSelectInputDirective.class );
	
	@Override
	public String getName() {
		return "themeforresource";
	}

	@Override
	public int getType() {
		return LINE;
	}

	@Override
	public boolean render( InternalContextAdapter context, Writer writer, Node node ) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

		String name = "";
		String id = null;
		
		try {
			try {
				if( node.jjtGetChild( 0 ) != null ) {
					id = (String) node.jjtGetChild( 0 ).value( context );
					//logger.debug( "Resource id: " + id );
				}
			} catch( Exception e ) {
				//logger.debug( "Id will not be set" );
			}
			
			if( node.jjtGetChild( 1 ) != null ) {
				name = String.valueOf( node.jjtGetChild( 1 ).value( context ) );
			} else {
				throw new IOException( "The name is mandatory" );
			}

			
		} catch( Exception e ) {
			if( name.length() == 0 ) {
				throw new IOException( "The name is mandatory" );
			}

			/* ... And we're done */
		}
		
		//logger.debug( "---- " + id + " ----" );
		
		AbstractTheme userTheme = null;
		if( id != null ) {
			NodeItem r = null;
			try {
				r = Core.getInstance().getNode( id );
			} catch( Exception e ) {
				logger.error( "Unable to load resource " + id + ": " + e.getMessage() );
			}

            /*
			try {
				userTheme = r.getTheme();
			} catch( ThemeDoesNotExistException e ) {
				logger.warn( "Unable to set theme for " + id );
			}
			*/
		}

        /*
		Collection<AbstractTheme> themes = Core.getInstance().getAllThemes();
        //List<AbstractTheme> themes = null;
		
		writer.write( "<select name=\"" + name + "\">" );
		
		//writer.writeToFile( "<option value=\"" + t.getName() + "\" selected>" + t.getName() + "</option>\n" );
		
		for( AbstractTheme t : themes ) {

			if( t.equals( userTheme ) ) {
				writer.write( "<option value=\"" + t.getName() + "\" selected>" + t.getName() + "</option>\n" );
			} else {
				writer.write( "<option value=\"" + t.getName() + "\">" + t.getName() + "</option>\n" );
			}

		}
		
		writer.write( "</select>\n" );
        */

		return true;
	}

}
