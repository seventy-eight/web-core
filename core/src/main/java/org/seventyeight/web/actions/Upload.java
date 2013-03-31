package org.seventyeight.web.actions;

import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.seventyeight.structure.Tuple;
import org.seventyeight.utils.Date;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.nodes.FileNode;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.utilities.ServletUtils;

import javax.servlet.AsyncContext;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author cwolfgang
 */
public class Upload implements Action {

    private static Logger logger = Logger.getLogger( Upload.class );

    private static SimpleDateFormat formatYear = new SimpleDateFormat( "yyyy" );
    private static SimpleDateFormat formatMonth = new SimpleDateFormat( "MM" );

    /*
    public void doMulti( Request request, Response response ) throws IOException {

    }
    */

    public void doInfo( Request request, Response response ) throws IOException {
        response.getWriter().write( 100 );
    }

    @PostMethod
    public void doUpload( Request request, Response response ) throws IOException {
        AsyncContext aCtx = request.startAsync( request, response );
        Executor uploadExecutor = Executors.newCachedThreadPool();

        String filename = request.getValue( "ax-file-name" );

        /* Create new file */
        FileNode f = null;
        try {
            f = (FileNode) Core.getInstance().getDescriptor( FileNode.class ).newInstance( filename );
        } catch( ItemInstantiationException e ) {
            throw new IOException( e );
        }

        String uid = request.getValue( "upload-identity" );
        f.setUploadIdentity( uid );
        f.setFilename( filename );
        f.save();

        logger.debug( "FFFIIFIFIFLE: " + f.getIdentifier() );

        logger.debug( "SERVLET THREAD: " + Thread.currentThread().getId() + " - " + Thread.currentThread().getName() );
        //uploadExecutor.execute( new ServletUtils.FileUploader( aCtx, request.getUser().getIdentifier().toString(), (String) f.getIdentifier() ) );
        uploadExecutor.execute( new ServletUtils.Copier( aCtx, request.getUser().getIdentifier().toString(), f.getIdentifier() ) );

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

        String strpath = "upload/" + pathPrefix + "/" + formatYear.format( now ) + "/" + formatMonth.format( now ) + "/" + ext;

        File path = new File( Core.getInstance().getPath(), strpath );
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
    public String getUrlName() {
        return "upload";
    }

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public Node getChild( String name ) throws NotFoundException {
        return null;
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
