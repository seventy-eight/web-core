package org.seventyeight.web.actions;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 * @deprecated ????
 */
public class Get implements Node, Parent {

    private Logger logger = LogManager.getLogger( Get.class );

    private Node parent;
    private Core core;

    public Get( Core core, Node parent ) {
        this.parent = parent;
        this.core = core;
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
    public Node getChild( String token ) {
        logger.debug( "Token is " + token );
        try {
            return core.getNodeById( this, token );
        } catch( Exception e ) {
            logger.log( Level.DEBUG, "Unable to get " + token, e );
            return null;
        }
    }

    @Override
    public String getMainTemplate() {
        return null;
    }
}
