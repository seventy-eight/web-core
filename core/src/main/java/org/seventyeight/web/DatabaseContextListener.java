package org.seventyeight.web;

import org.apache.log4j.Logger;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.SavingException;
import org.seventyeight.web.utilities.Installer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author cwolfgang
 */
//@WebListener
public abstract class DatabaseContextListener<T extends Core> implements ServletContextListener {
    private static Logger logger = Logger.getLogger( DatabaseContextListener.class );

    private long seconds;

    public void contextDestroyed( ServletContextEvent arg0 ) {
        synchronized( DatabaseContextListener.class ) {
            long duration = System.currentTimeMillis() - seconds;
            System.out.println( "Shutting down after " + ( duration / 1000 ) + " seconds" );
        }
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

            /* Specialized templates paths comes first */
            String templatePathStr = System.getProperty( "templatePath", null );
            if( templatePathStr != null ) {
                logger.debug( "Template path: " + templatePathStr );
                templatePaths.add( new File( templatePathStr ) );
            }

            for( File plugin : plugins ) {
                File f1 = new File( plugin, "templates" );
                if( f1.exists() ) {
                    templatePaths.add( f1 );
                }

                File f2 = new File( plugin, "lib" );
                if( f2.exists() ) {
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

            logger.info( "Loading templates" );
            core.getTemplateManager().setTemplateDirectories( templatePaths );
            logger.debug( core.getTemplateManager().toString() );
            core.getTemplateManager().initialize();
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


        /* INSTALL */
        Installer installer = new Installer();
        try {
            installer.install();
        } catch( Exception e ) {
            throw new IllegalStateException( e );
        }

        /* Asynch */
        //Executor executor = new ThreadPoolExecutor(10, 10, 50000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100));
        Executor executor =  Executors.newCachedThreadPool();
        sce.getServletContext().setAttribute( "executor", executor );
    }


    protected class FF implements FilenameFilter {
        @Override
        public boolean accept( File dir, String name ) {
            return name.endsWith( ".vm" );
        }
    }
}
