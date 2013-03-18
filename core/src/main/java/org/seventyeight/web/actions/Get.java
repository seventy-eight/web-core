package org.seventyeight.web.actions;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Actionable;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.NodeItem;

/**
 * @author cwolfgang
 */
public class Get extends Actionable {

    private Logger logger = Logger.getLogger( Get.class );

    @Override
    public Object getDynamic( NodeItem parent, String token ) {
        try {
            return Core.getInstance().getNodeById( token );
        } catch( ItemInstantiationException e ) {
            logger.log( Level.DEBUG, "Unable to get " + token, e );
            return null;
        }
    }
}
