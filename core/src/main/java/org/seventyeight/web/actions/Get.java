package org.seventyeight.web.actions;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class Get implements Action {

    private Logger logger = Logger.getLogger( Get.class );

    private Node parent;

    public Get( Node parent ) {
        this.parent = parent;
    }

    @Override
    public String getDisplayName() {
        return "Get node";
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public String getUrlName() {
        return "get";
    }

    @Override
    public Node getChild( String token ) {
        logger.debug( "Token is " + token );
        try {
            return Core.getInstance().getNodeById( this, token );
        } catch( ItemInstantiationException e ) {
            logger.log( Level.DEBUG, "Unable to get " + token, e );
            return null;
        }
    }

    @Override
    public String getMainTemplate() {
        return "org/seventyeight/web/main.vm";
    }
}
