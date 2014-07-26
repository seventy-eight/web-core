package org.seventyeight.web.utilities;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.structure.Tuple;
import org.seventyeight.utils.FileUtilities;
import org.seventyeight.web.Core;
import org.seventyeight.web.actions.Upload;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.nodes.FileResource;
import org.seventyeight.web.servlet.Request;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class ServletUtils {

    private static Logger logger = LogManager.getLogger( ServletUtils.class );

    public static class Copier implements Runnable {
        AsyncContext ctx;
        String pathPrefix;
        String id;

        private Core core;

        public Copier( Core core, AsyncContext ctx, String pathPrefix, String id ) {
            this.ctx = ctx;
            this.pathPrefix = pathPrefix;
            this.id = id;
            this.core = core;
        }

        public void run() {
            HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
            logger.debug( "REQUEST IS " + request );

            FileResource obj = null;
            try {
                obj = (FileResource) core.getNodeById( core.getRoot(), id );
            } catch( Exception e ) {
                logger.log( Level.FATAL, "Failed to get node when uploading", e );
                return;
            }

            obj.getDocument().set( "file", null );

            logger.fatal( "TOIIIIIIIIIIITLOTLLTELE: " + obj.getTitle() );
            Tuple<File, File> files = Upload.generateFile( obj.getTitle(), pathPrefix, core.getUploadPath() );
            obj.getDocument().set( "file", files.getFirst().toString() );
            obj.save();

            FileOutputStream out = null;
            try {
                out = new FileOutputStream( files.getSecond() );
                //out.write( IOUtils.readFully( request.getInputStream(), -1, false ) );
                FileUtilities.writeToFile( request.getInputStream(), files.getSecond() );
            } catch( IOException e ) {
                logger.error( "Unable to copy file", e );
            } finally {
                if( out != null ) {
                    try {
                        out.close();
                    } catch( IOException e ) {
                        e.printStackTrace();
                    }
                }

                logger.info( "File was written, " + files.getFirst().toString() );
                ctx.complete();
            }
        }
    }

    public static String generate( String filename ) {
        int i = filename.lastIndexOf( '.' );
        if( i < 0 ) {
            return "dot";
        } else {
            return filename.substring( i );
        }
    }

    /**
     * Upload files from a form, return the filenames.
     */
    public static List<String> upload( Request request, File destinationPath, boolean useFieldName, int limit ) throws Exception {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload( factory );
        List<FileItem> items = upload.parseRequest( request );

        List<String> uploadedFiles = new ArrayList<String>( items.size() );

        //for( FileItem item : items ) {
        int size = limit > 0 ? limit : items.size();
        for( int i = 0 ; i < size ; ++i ) {
            FileItem item = items.get( i );
            if( !item.isFormField() ) {
                String filename = useFieldName ? replace( item.getName(), item.getFieldName() ) : item.getName();
                File destination = new File( destinationPath + "/" + generate( filename ), filename );
                item.write( destination );
                uploadedFiles.add( filename );
            }
        }

        return uploadedFiles;
    }

    /*
    public static List<FileResource> upload2( Request request, File destinationPath, String relativePath, boolean useFieldName, int limit ) throws Exception {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload( factory );
        List<FileItem> items = upload.parseRequest( request );

        List<FileResource> uploadedFiles = new ArrayList<FileResource>( items.size() );

        //for( FileItem item : items ) {
        int size = limit > 0 ? limit : items.size();
        for( int i = 0 ; i < size ; ++i ) {
            FileItem item = items.get( i );
            if( !item.isFormField() ) {
                String filename = useFieldName ? replace( item.getName(), item.getFieldName() ) : item.getName();
                File path = new File( new File( destinationPath, relativePath ), generate( filename ) );
                File filePath = new File( path, filename );
                item.write( filePath );

                FileResource fr = FileResource.create( filename );
                fr.setPath( relativePath );
                fr.setFilename( filename );
                fr.save();

                uploadedFiles.add( fr );
            }
        }

        return uploadedFiles;
    }
    */

    public static String replace( String filename, String replace ) {
        return filename.replaceAll( "^.*?(\\..*)?$", replace + "$1" );
    }

    /*
    public static class FileUploader implements Runnable {
        AsyncContext ctx;
        String pathPrefix;
        String id;

        public FileUploader( AsyncContext ctx, String pathPrefix, String id ) {
            this.ctx = ctx;
            this.pathPrefix = pathPrefix;
            this.id = id;
        }

        public void run() {
            HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
            logger.debug( "REQUEST IS " + request );

            AbstractNode obj = null;
            try {
                obj = (AbstractNode) Core.getInstance().getNodeById( Core.getInstance(), id );
            } catch( Exception e ) {
                logger.log( Level.FATAL, "Failed to get node when uploading", e );
                return;
            }

            obj.getDocument().set( "file", null );

            List<FileItem> items = null;
            FileItemFactory factory = new DiskFileItemFactory();
            FileUploadListener listener = new FileUploadListener();
            HttpSession session = request.getSession();
            session.setAttribute( "listener", listener );

            ServletFileUpload upload = new ServletFileUpload( factory );
            upload.setProgressListener( listener );

            try {
                items = upload.parseRequest( request );
            } catch( FileUploadException e ) {
                e.printStackTrace();
            }
            for( FileItem item : items ) {
                if( !item.isFormField() ) {
                    Tuple<File, File> files = Upload.generateFile( item.getName(), pathPrefix );
                    try {
                        item.write( files.getSecond() );
                    } catch( Exception e ) {
                        e.printStackTrace();
                    }

                    obj.getDocument().set( "file", files.getFirst().toString() );
                }
            }

            obj.save();
        }

    }
    */

}
