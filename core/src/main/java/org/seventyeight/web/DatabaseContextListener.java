package org.seventyeight.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.utils.TimeUtils;
import org.seventyeight.web.utilities.Installer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author cwolfgang
 */
//@WebListener
public abstract class DatabaseContextListener<T extends Core> implements ServletContextListener {
    private static Logger logger = LogManager.getLogger( DatabaseContextListener.class );

    private long seconds;

    protected List<String> extraTemplatePaths = new ArrayList<String>(  );

    public void contextDestroyed( ServletContextEvent arg0 ) {
        synchronized( DatabaseContextListener.class ) {
            long duration = System.currentTimeMillis() - seconds;
            System.out.println( "Shutting down after " + ( duration / 1000 ) + " seconds" );
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run() {
                long duration = System.currentTimeMillis() - seconds;
                //System.out.println( "Shutting down after " + ( duration / 1000 ) + " seconds" );
                System.out.println( "Shutting down after " + TimeUtils.getTimeString( duration ) );
            }
        } );
    }

    public abstract T getCore( File path, String dbname ) throws CoreException;

    public void contextInitialized( ServletContextEvent sce ) {

        String spath = sce.getServletContext().getRealPath( "" );
        logger.info( "Path: " + spath );

        seconds = System.currentTimeMillis();

        List<File> templatePaths = new ArrayList<File>();

        File path = new File( spath );

        Core core = null;
        try {
            core = getCore( path, "seventyeight" ).initialize();
        } catch( CoreException e ) {
            e.printStackTrace();
            logger.fatal( "Failed to initialize core", e );
        }

        try {
            List<File> plugins = core.extractPlugins( core.getPath() );

                /* Paths added first is served first */
            //gd.getTemplateManager().addStaticPath( new File( "C:/projects/graph-dragon/war/src/main/webapp/static" ) );
            //gd.getTemplateManager().addStaticPath( new File( gd.getPath(), "static" ) );

            String staticPathStr = System.getProperty( "static", null );
            File staticPath = null;
            if( staticPathStr != null ) {
                staticPath = new File( staticPathStr );
            } else {
                staticPath = new File( path, "static" );
            }

            logger.info( "Static path: " + staticPath.getAbsolutePath() );
            core.getTemplateManager().addStaticPath( staticPath );

            //paths.add( new File( "/home/wolfgang/projects/graph-dragon/system/target/classes/templates" ) );
            //paths.add( new File( "C:/projects/graph-dragon/system/target/classes/templates" ) );

            logger.info( "[System] Adding additional resources" );
            String[] ar = System.getProperty( "additional", "" ).split( "\\s*,\\s*" );
            for( String additional : ar ) {
                logger.info( "[System] Adding resources from " + additional );
                File afile = new File( additional );

                File atemplates = new File( additional, "templates" );
                if( atemplates.exists() ) {
                    logger.info( "[Template] Adding templates " + atemplates );
                    templatePaths.add( atemplates );
                }

                File alibs = new File( additional, "lib" );
                if( alibs.exists() ) {
                    logger.info( "[Template] Adding libs " + alibs );
                    templatePaths.add( alibs );

                    File[] libs = alibs.listFiles( new FF() );
                    for( File lib : libs ) {
                        Core.getInstance().getTemplateManager().addTemplateLibrary( lib.getName() );
                    }
                }
            }


            logger.info( "[System] Loading plugins" );
            for( File plugin : plugins ) {
                logger.info( "Loading " + plugin );

                /* Is it overridden? */
                String overridden = System.getProperty( plugin.getName(), null );
                if( overridden != null ) {
                    logger.info( "Overriding " + plugin + " to " + overridden );
                    plugin = new File( overridden );
                }

                File f1 = new File( plugin, "templates" );
                if( f1.exists() ) {
                    templatePaths.add( f1 );
                    logger.info( "[Template] Adding templates " + f1 );
                }

                File f2 = new File( plugin, "lib" );
                if( f2.exists() ) {
                    logger.info( "[Template] Adding libs " + f2 );
                    templatePaths.add( f2 );

                    File[] libs = f2.listFiles( new FF() );
                    for( File lib : libs ) {
                        Core.getInstance().getTemplateManager().addTemplateLibrary( lib.getName() );
                    }
                }
            }


            //paths.add( new File( "C:/Users/Christian/projects/web-core/system/src/main/resources/templates" ) );


            /* LIB */
            //templatePaths.add( new File( "C:/Users/Christian/projects/web-core/system/src/main/resources/lib" ) );
            //Core.getInstance().getTemplateManager().addTemplateLibrary( "form.vm" );

            //paths.add( new File( "C:/projects/graph-dragon/system/target/classes/templates" ) );

            logger.info( "Loading plugins" );
            //core.getClassLoader().addUrls( new URL[]{ new File( spath, "WEB-INF/lib/core.jar" ).toURI().toURL() } );
            core.getPlugins( plugins );


        } catch( IOException e ) {
            e.printStackTrace();
        }

        /* Themes path */
        String themePathStr = System.getProperty( "theme", null );
        File themePath = null;
        if( themePathStr != null ) {
            themePath = new File( themePathStr );
        } else {
            themePath = new File( path, "themes" );
        }

        logger.info( "Themes path: " + themePath.getAbsolutePath() );
        Core.getInstance().setThemesPath( themePath );

        /*
        String libPathStr = System.getProperty( "lib", null );
        if( libPathStr != null ) {
            logger.debug( "Lib path is set to " + libPathStr );
            File libpath = new File( libPathStr );
            if( libpath.exists() ) {
                templatePaths.add( libpath );

                File[] libs = libpath.listFiles( new FF() );
                for( File lib : libs ) {
                    logger.debug( "Adding lib " + lib );
                    Core.getInstance().getTemplateManager().addTemplateLibrary( lib.getName() );
                }
            }
        }
        */

        logger.info( "Loading templates: " + templatePaths );
        core.getTemplateManager().setTemplateDirectories( templatePaths );
        logger.debug( core.getTemplateManager().toString() );
        core.getTemplateManager().initialize();

        try {
            install();
        } catch( DatabaseException e ) {
            logger.fatal( "Unable to install", e );
        }

        /* Asynch */
        //Executor executor = new ThreadPoolExecutor(10, 10, 50000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100));
        Executor executor =  Executors.newCachedThreadPool();
        sce.getServletContext().setAttribute( "executor", executor );


        registerShutdownHook();
    }

    /**
     * Default implementation of install. Could/should be overridden.
     */
    protected void install() throws DatabaseException {
        /* INSTALL */
        Installer installer = new Installer();
        try {
            installer.install();
        } catch( Exception e ) {
            throw new IllegalStateException( e );
        }
    }

    protected class FF implements FilenameFilter {
        @Override
        public boolean accept( File dir, String name ) {
            return name.endsWith( ".vm" );
        }
    }
}
