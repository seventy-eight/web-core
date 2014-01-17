package org.seventyeight.web.nodes.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.actions.ResourcesAction;
import org.seventyeight.web.extensions.StartupListener;
import org.seventyeight.web.extensions.search.SearchFormatter;
import org.seventyeight.web.model.Node;

import java.util.List;

/**
 * @author cwolfgang
 */
public class SearchFormatListener implements StartupListener {

    private static Logger logger = LogManager.getLogger( FileTypeListener.class );

    @Override
    public void onStartup() {
        logger.debug( "Gathering file types" );
        List<SearchFormatter> fileTypes = Core.getInstance().getExtensions( SearchFormatter.class );

        //List<ResourcesAction> ra = Core.getInstance().getExtensions( ResourcesAction.class );
        ResourcesAction ra = (ResourcesAction) Core.getInstance().getChild( "resources" );
        if( ra == null ) {
            throw new IllegalStateException( "No Resources Action instance found" );
        }
        for( SearchFormatter fileType : fileTypes ) {
            ra.addFormatter( fileType );
        }
    }
}
