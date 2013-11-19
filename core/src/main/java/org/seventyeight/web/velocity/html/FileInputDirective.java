package org.seventyeight.web.velocity.html;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Writer;

public class FileInputDirective extends AbstractInputDirective {

	private static Logger logger = LogManager.getLogger( FileInputDirective.class );
	
	@Override
	public String getName() {
		return "inputfile";
	}

	@Override
	public int getType() {
		return LINE;
	}


	@Override
	protected boolean input( Writer writer, String name, String value, int maxLength, boolean readonly, String onclick ) throws IOException {
		writer.write( "<input type=\"file\" name=\"" + name + "\" value=\"" + value + "\" " + ( maxLength > 0 ? "maxlength=\"" + maxLength + "\"" : "" ) + " onclick=\"" + onclick + "\"" + ( readonly ? " readonly" : "" ) + ">" );
		return true;
	}

}
