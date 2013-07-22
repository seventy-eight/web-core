package org.seventyeight.web.actions;

import org.apache.log4j.Logger;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;

/**
 * @author cwolfgang
 */
public class Search implements Node {

    private static Logger logger = Logger.getLogger( Search.class );

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public String getDisplayName() {
        return "Search";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    @PostMethod
    public void doIndex( Request request, Response response ) throws IOException {
        String query = request.getValue( "query", null );
        logger.debug( "Searching for " + query );

        if( query != null ) {

        } else {
            response.sendRedirect( "" );
        }
    }
}
