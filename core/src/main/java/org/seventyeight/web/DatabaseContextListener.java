package org.seventyeight.web;

import org.apache.log4j.Logger;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.SavingException;
import org.seventyeight.web.utilities.Installer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
@WebListener
public abstract class DatabaseContextListener<T extends Core> implements ServletContextListener {
    private static Logger logger = Logger.getLogger( DatabaseContextListener.class );

    private long seconds;

    public void contextDestroyed( ServletContextEvent arg0 ) {
        synchronized( DatabaseContextListener.class ) {
            long duration = System.currentTimeMillis() - seconds;
            System.out.println( "Shutting down after " + ( duration / 1000 ) + " seconds" );
        }
    }

    public abstract T getCore( File path, String dbname );

    public void contextInitialized( ServletContextEvent sce ) {

        String spath = sce.getServletContext().getRealPath( "" );
        logger.info( "Path: " + spath );

        seconds = System.currentTimeMillis();

        List<File> paths = new ArrayList<File>();

        File path = new File( spath );

        Core core = getCore( path, "seventyeight" );

        try {
            List<File> plugins = core.extractPlugins( core.getPath() );

                /* Paths added first is served first */
            //gd.getTemplateManager().addStaticPath( new File( "C:/projects/graph-dragon/war/src/main/webapp/static" ) );
            //gd.getTemplateManager().addStaticPath( new File( gd.getPath(), "static" ) );
            core.getTemplateManager().addStaticPath( new File( "C:/Users/Christian/projects/web-core/cms-war/src/main/webapp/static" ) );


            //paths.add( new File( "/home/wolfgang/projects/graph-dragon/system/target/classes/templates" ) );
            //paths.add( new File( "C:/projects/graph-dragon/system/target/classes/templates" ) );



                /*
                for( File plugin : plugins ) {
                    logger.debug( "Adding " + plugin );
                    paths.add( new File( plugin, "themes" ) );
                }
                */

            paths.add( new File( "C:/Users/Christian/projects/web-core/system/src/main/resources/templates" ) );

                /* LIB */
            paths.add( new File( "C:/Users/Christian/projects/web-core/system/src/main/resources/lib" ) );
            Core.getInstance().getTemplateManager().addTemplateLibrary( "form.vm" );

            //paths.add( new File( "C:/projects/graph-dragon/system/target/classes/templates" ) );

            logger.info( "Loading plugins" );
            core.getClassLoader().addUrls( new URL[]{ new File( spath, "WEB-INF/lib/core.jar" ).toURI().toURL() } );
            core.getPlugins( plugins );

            logger.info( "Loading templates" );
            core.getTemplateManager().setTemplateDirectories( paths );
            logger.debug( core.getTemplateManager().toString() );
            core.getTemplateManager().initialize();
        } catch( IOException e ) {
            e.printStackTrace();
        }

            /* Adding action handlers */
            /*
            //GraphDragon.getInstance().addActionHandler( "system", new SystemHandler() );
            SeventyEight.getInstance().addTopLevelGizmo( new ResourceAction() );
            SeventyEight.getInstance().addTopLevelGizmo( new ResourcesAction() );
            SeventyEight.getInstance().addTopLevelGizmo( new StaticFileHandler() );
            SeventyEight.getInstance().addTopLevelGizmo( new UploadHandler() );

            //SeventyEight.getInstance().addTopLevelGizmo( "debate", new DebateHandler() );

            SeventyEight.getInstance().addTopLevelGizmo( new DatabaseBrowser() );

            SeventyEight.getInstance().addTopLevelGizmo( new ThemeFileHandler() );
            SeventyEight.getInstance().addTopLevelGizmo( new LoginHandler() );
            */

            /* Post actions */
        Core.getInstance().setThemesPath( new File( "C:/Users/Christian/projects/web-core/system/src/main/resources/themes" ) );


            /* INSTALL */
        Installer installer = new Installer();
        try {
            installer.install();
        } catch( Exception e ) {
            throw new IllegalStateException( e );
        }
    }
}
