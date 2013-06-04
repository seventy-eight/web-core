package org.seventyeight.web.utilities;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.seventyeight.structure.Tuple;
import org.seventyeight.utils.FileUtilities;
import org.seventyeight.web.Core;
import org.seventyeight.web.actions.Upload;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.PersistedObject;
import org.seventyeight.web.nodes.FileNode;
import org.seventyeight.web.servlet.Request;
import sun.misc.IOUtils;

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

    private static Logger logger = Logger.getLogger( ServletUtils.class );

    public static class Copier implements Runnable {
        AsyncContext ctx;
        String pathPrefix;
        String id;

        public Copier( AsyncContext ctx, String pathPrefix, String id ) {
            this.ctx = ctx;
            this.pathPrefix = pathPrefix;
            this.id = id;
        }

        public void run() {
            HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
            logger.debug( "REQUEST IS " + request );

            FileNode obj = null;
            try {
                obj = (FileNode) Core.getInstance().getNodeById( Core.getInstance(), id );
            } catch( ItemInstantiationException e ) {
                logger.log( Level.FATAL, "Failed to get node when uploading", e );
                return;
            }

            obj.getDocument().set( "file", null );

            logger.fatal( "TOIIIIIIIIIIITLOTLLTELE: " + obj.getTitle() );
            Tuple<File, File> files = Upload.generateFile( obj.getTitle(), pathPrefix );
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
                File destination = new File( destinationPath, filename );
                item.write( destination );
                uploadedFiles.add( filename );
            }
        }

        return uploadedFiles;
    }

    public static String replace( String filename, String replace ) {
        return filename.replaceAll( "^.*?(\\..*)?$", replace + "$1" );
    }

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
            } catch( ItemInstantiationException e ) {
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

}
