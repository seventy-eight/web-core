package org.seventyeight.web.velocity.html;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;

public class WidgetDirective extends Directive {

	private static Logger logger = LogManager.getLogger( WidgetDirective.class );
	
	@Override
	public String getName() {
		return "widget";
	}

	@Override
	public int getType() {
		return LINE;
	}

	@Override
	public boolean render( InternalContextAdapter context, Writer writer, Node node ) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

		String name = "";
		
		try {
			if( node.jjtGetChild( 0 ) != null ) {
				logger.debug( "NODE[0]=" + node.jjtGetChild( 0 ) + "/" + node.jjtGetChild( 0 ).value( context ) );
				name = String.valueOf( node.jjtGetChild( 0 ).value( context ) );
			} else {
				throw new IOException( "The name is mandatory" );
			}
			
		} catch( Exception e ) {
			if( name.length() == 0 ) {
				throw new IOException( "The name is mandatory" );
			}
			/* ... And we're done */
		}
		
		//writer.writeToFile( "<input type=\"text\" name=\"" + name + "\" value=\"" + value + "\" " + ( maxLength > 0 ? "maxlength=\"" + maxLength + "\"" : "" ) + " onclick=\"" + onClick + "\"" + ( readonly ? " readonly" : "" ) + ">" );

        writer.write( "<div id=\"widgetContainer\">\n" +
                      "</div>\n" +
                      "<script type=\"text/javascript\">\n" +
                      "    $(function() {\n" +
                      "        $.ajax({\n" +
                      "            type: \"GET\",\n" +
                      "            url: \"/widgets/Activity widget/view\",\n" +
                      "            success: function(data, textStatus, jqxhr){$('#widgetContainer').html(data)},\n" +
                      "            error: function(ajax, text, error) {alert(error)}\n" +
                      "        });\n" +
                      "    });\n" +
                      "</script>" );

		return true;
	}


}
