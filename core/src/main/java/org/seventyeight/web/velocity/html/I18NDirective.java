package org.seventyeight.web.velocity.html;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.seventyeight.web.handlers.Dictionary;

import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;

public class I18NDirective extends Directive {

	private static Logger logger = LogManager.getLogger( I18NDirective.class );
	
	@Override
	public String getName() {
		return "i18n";
	}

	@Override
	public int getType() {
		return LINE;
	}

	@Override
	public boolean render( InternalContextAdapter context, Writer writer, Node node ) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

		String text = "";
		//Locale locale = (Locale) context.get( "locale" );
		text = String.valueOf( node.jjtGetChild( 0 ).value( context ) );

        ResourceBundle rb = (ResourceBundle) context.get( "rb" );
		if( rb != null ) {
			writer.write( rb.getString( text ) );
		} else {
			writer.write( text );
		}

		return true;
	}

}
