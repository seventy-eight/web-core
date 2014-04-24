package org.seventyeight.web.velocity.html;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.seventyeight.web.model.Describable;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.servlet.Request;

import java.io.IOException;
import java.io.Writer;

public class RenderDescriptorDirective extends Directive {

	private static Logger logger = LogManager.getLogger( RenderDescriptorDirective.class );
	
	@Override
	public String getName() {
		return "renderDescriptor";
	}

	@Override
	public int getType() {
		return LINE;
	}

	@Override
	public boolean render( InternalContextAdapter context, Writer writer, Node node ) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        logger.debug( "Rendering descriptor" );
		Descriptor d = null;
        Describable describable = null;
        Boolean expanded = false;
		
		try {
			if( node.jjtGetChild( 0 ) != null ) {
				d = (Descriptor) node.jjtGetChild( 0 ).value( context );
			} else {
				throw new IOException( "Argument not a descriptor" );
			}

            if( node.jjtGetChild( 1 ) != null ) {
                describable = (Describable) node.jjtGetChild( 1 ).value( context );
            } else {
                throw new IOException( "Argument not an item" );
            }

            if( node.jjtGetChild( 2 ) != null ) {
                expanded = (Boolean)node.jjtGetChild( 2 ).value( context );
            } else {
                throw new IOException( "Argument not boolean" );
            }

		} catch( Exception e ) {
            logger.debug( e );
		}

        Request request = (Request) context.get( "request" );

        logger.fatal( "ITEM IS " + describable );

        try {
            if( describable == null ) {
                writer.write( d.getConfigurationPage( request, null ) );
            } else {
                // Check describable
                if(!d.getClazz().isInstance( describable )) {
                    logger.debug( "{} is not instance of {}", d, d.getClazz() );
                    describable = d.getDescribable( describable.getParent(), describable.getDocument() );
                    logger.debug( "Returned describabale: {}", describable );
                }

                writer.write( d.getConfigurationPage( request, describable ) );
            }
        } catch( Exception e ) {
            throw new IOException( e );
        }


        /* get the extension node */
        /*
        List<org.seventyeight.database.Node> nodes = item.getExtensionNodesByClass( d.getClazz() );

        logger.debug( "Configured instances: " + nodes.size() );

        for( org.seventyeight.database.Node n : nodes ) {
            try {
                AbstractExtension extension = SeventyEight.getInstance().getDatabaseItem( n );
                writer.write( d.getConfigurationPage( request, extension ) );
            } catch( TemplateDoesNotExistException e ) {
                logger.warn( e );
                writer.write( e.getMessage() );
            } catch( CouldNotLoadObjectException e ) {
                e.printStackTrace();
            }
        }

        if( nodes.size() == 0 ) {
            logger.fatal( "NO NODES" );
            try {
                writer.write( d.getConfigurationPage( request, null ) );
            } catch( TemplateDoesNotExistException e ) {
                logger.warn( e );
                writer.write( e.getMessage() );
            }
        }
        */

        return true;
	}

}
