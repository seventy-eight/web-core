package org.seventyeight.web.nodes.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.Core;
import org.seventyeight.web.actions.WidgetAction;
import org.seventyeight.web.extensions.StartupListener;
import org.seventyeight.web.extensions.filetype.FileType;
import org.seventyeight.web.model.Widget;
import org.seventyeight.web.nodes.FileResource;

import java.util.List;

/**
 * @author cwolfgang
 */
public class WidgetListener implements StartupListener {

    private static Logger logger = LogManager.getLogger( WidgetListener.class );

    private Core core;

    public WidgetListener( Core core ) {
        this.core = core;
    }

    @Override
    public void onStartup() {
        logger.debug( "Gathering widgets" );
        List<Widget> widgets = core.getExtensions( Widget.class );

        WidgetAction wa = (WidgetAction) core.getRoot().getChild( "widgets" );
        if( wa == null ) {
            throw new IllegalStateException( "No Widgets action instance found" );
        }

        for( Widget widget : widgets ) {
            wa.addWidget( widget );
        }
    }
}
