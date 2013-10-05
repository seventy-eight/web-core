package org.seventyeight.web.actions;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.structure.Tuple;
import org.seventyeight.utils.Date;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;
import org.seventyeight.web.nodes.FileResource;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.utilities.ServletUtils;

import javax.servlet.AsyncContext;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author cwolfgang
 */
public class Upload implements Node {

    private static Logger logger = Logger.getLogger( Upload.class );

    private static SimpleDateFormat formatYear = new SimpleDateFormat( "yyyy" );
    private static SimpleDateFormat formatMonth = new SimpleDateFormat( "MM" );

    /*
    public void doMulti( Request request, Response response ) throws IOException {

    }
    */

    public void doInfo( Request request, Response response ) throws IOException, ItemInstantiationException {
        String id = request.getValue( "id" );
        FileResource fileResource = getFileByUploadId( this, id );
        try {

            File file = fileResource.getFile();
            long currentSize = file.length();
            Double ratio = Math.floor( ( (double)currentSize / (double) fileResource.getExpectedFileSize() ) * 10000 ) / 100.0;

            /*
            logger.fatal( "File: " + file );
            logger.fatal( "Exists: " + file.exists() );
            logger.fatal( "File node: " + fileResource );
            logger.fatal( "Current: " + currentSize );
            logger.fatal( "Expected: " + fileResource.getExpectedFileSize() );
            logger.fatal( "Ratio: " + ratio );
            */

            response.getWriter().write( ratio.toString() );
        } catch( Exception e ) {
            /* TODO do something clever instead */
            logger.fatal( "Failed due to " + e.getMessage() );
            response.getWriter().write( "0" );
        }
    }

    protected static FileResource getFileByUploadId( Node parent, String uploadId ) throws ItemInstantiationException {
        MongoDocument doc = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).findOne( new MongoDBQuery().is( "uploadID", uploadId ) );

        if( doc != null ) {
            return new FileResource( parent, doc );
        } else {
            throw new ItemInstantiationException( "The File with upload id " + uploadId + " not found" );
        }
    }

    @PostMethod
    public void doUpload( Request request, Response response ) throws IOException, SavingException, ItemInstantiationException, ClassNotFoundException {
        AsyncContext aCtx = request.startAsync( request, response );


        String filename = request.getValue( "ax-file-name" );

        /* Somehow get the right uploadable descriptor */
        List<Descriptor> descriptors = Core.getInstance().getExtensionDescriptors( Uploadable.class );
        for( Descriptor d : descriptors ) {

        }

        /* Create new file */
        UploadableNode f = null;
        try {
            FileResource.FileDescriptor d = Core.getInstance().getDescriptor( FileResource.class );
            f = (UploadableNode) d.newInstance( filename );
        } catch( ItemInstantiationException e ) {
            throw new IOException( e );
        }

        String uid = request.getValue( "upload-identity" );
        long byteSize = Long.valueOf( (String)request.getValue( "ax-fileSize" ) );
        f.setUploadIdentity( uid );
        f.setFilename( filename );
        f.setExpectedFileSize( byteSize );
        //f.setOwner( request.getUser() );
        //f.save();
        f.save( request, null );

        logger.debug( "File identifier: " + f.getIdentifier() );

        logger.debug( "SERVLET THREAD: " + Thread.currentThread().getId() + " - " + Thread.currentThread().getName() );
        //uploadExecutor.execute( new ServletUtils.FileUploader( aCtx, request.getUser().getIdentifier().toString(), (String) f.getIdentifier() ) );

        Executor executor = (Executor)request.getServletContext().getAttribute("executor");
        executor.execute( new ServletUtils.Copier( aCtx, request.getUser().getIdentifier().toString(), f.getIdentifier() ) );

        logger.debug( "Executed: " + f.getIdentifier() );

        response.getWriter().println( "done" );
    }

    /**
     * @param filename
     * @param pathPrefix
     * @return First is a {@link java.io.File} relative to the context path, and the second is an absolute file.
     */
    public static Tuple<File, File> generateFile( String filename, String pathPrefix ) {
        Date now = new Date();
        int mid = filename.lastIndexOf( "." );
        String fname = filename;
        String ext = null;
        if( mid > -1 ) {
            ext = filename.substring( mid + 1, filename.length() );
            fname = filename.substring( 0, mid );
        }

        String strpath = pathPrefix + "/" + formatYear.format( now ) + "/" + formatMonth.format( now ) + "/" + ext;

        File path = new File( Core.getInstance().getUploadPath(), strpath );
        File relativePath = new File( strpath, filename );
        logger.debug( "Trying to create path " + path );
        path.mkdirs();
        File file = new File( path, filename );
        int cnt = 0;
        while( file.exists() ) {
            file = new File( path, fname + "_" + cnt + ( ext != null ? "." + ext : "" ) );
            cnt++;
        }

        logger.debug( "FILE: " + file.getAbsolutePath() );
        logger.debug( "FILE: " + relativePath );

        return new Tuple<File, File>( relativePath, file );

    }

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public String getDisplayName() {
        return "Upload";
    }

    @Override
    public String getMainTemplate() {
        return "org/seventyeight/web/main.vm";
    }
}
