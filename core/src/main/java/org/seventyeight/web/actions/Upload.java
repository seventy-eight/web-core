package org.seventyeight.web.actions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.structure.Tuple;
import org.seventyeight.utils.Date;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.*;
import org.seventyeight.web.model.extensions.NodeListener;
import org.seventyeight.web.nodes.FileResource;
import org.seventyeight.web.nodes.ImageUploadsWrapper;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.utilities.UploadHandler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Upload implements Node {

    private static Logger logger = LogManager.getLogger( Upload.class );

    private static SimpleDateFormat formatYear = new SimpleDateFormat( "yyyy" );
    private static SimpleDateFormat formatMonth = new SimpleDateFormat( "MM" );

    private Core core;

    public Upload( Core core ) {
        this.core = core;
    }

    /*
    public void doMulti( Request request, Response response ) throws IOException {

    }
    */

    /*
    public void doInfo( Request request, Response response ) throws IOException, ItemInstantiationException {
        String id = request.getValue( "id" );
        FileResource fileResource = getFileByUploadId( this, id );
        try {

            File file = fileResource.getFile();
            long currentSize = file.length();
            Double ratio = Math.floor( ( (double)currentSize / (double) fileResource.getExpectedFileSize() ) * 10000 ) / 100.0;

            response.getWriter().write( ratio.toString() );
        } catch( Exception e ) {
            logger.fatal( "Failed due to " + e.getMessage() );
            response.getWriter().write( "0" );
        }
    }
    */

    protected static FileResource getFileByUploadId( Core core, Node parent, String uploadId ) throws ItemInstantiationException {
        MongoDocument doc = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).findOne( new MongoDBQuery().is( "uploadID", uploadId ) );

        if( doc != null ) {
            return new FileResource( core, parent, doc );
        } else {
            throw new ItemInstantiationException( "The File with upload id " + uploadId + " not found" );
        }
    }

    @PostMethod
    public void doUpload( Request request, Response response ) throws IOException, SavingException, ItemInstantiationException, ClassNotFoundException {
        //AsyncContext aCtx = request.startAsync( request, response );
        response.setRenderType( Response.RenderType.NONE );

        /* Somehow get the right uploadable descriptor */
        List<Descriptor> descriptors = core.getExtensionDescriptors( Uploadable.class );
        for( Descriptor d : descriptors ) {

        }

        logger.debug( "SERVLET THREAD: " + Thread.currentThread().getId() + " - " + Thread.currentThread().getName() );
        //uploadExecutor.execute( new ServletUtils.FileUploader( aCtx, request.getUser().getIdentifier().toString(), (String) f.getIdentifier() ) );

        //Executor executor = (Executor)request.getServletContext().getAttribute("executor");
        //executor.execute( new ServletUtils.Copier( aCtx, request.getUser().getIdentifier().toString(), f.getIdentifier() ) );
        //executor.execute( new ServletUtils.FileUploader( aCtx, request.getUser().getIdentifier().toString(), f.getIdentifier() ) );
        //executor.execute( new UploadHandler( aCtx, request.getUser().getIdentifier().toString(), UploadHandler.commonsUploader, UploadHandler.DefaultFilenamer ) );


        List<FileItem> items = null;
        try {
            items = UploadHandler.commonsUploader.getItems( request );
        } catch( FileUploadException e ) {
            logger.error( e.getMessage() );
            return;
        }

        UploadResponse r = new UploadResponse();
        r.name = "NAME";
        r.rid = "";
        r.size = 1000;
        r.length = 2000;
        r.deleteUrl = "http://jajdjawd";
        r.thumbnailUrl = "/images/none";

        JsonObject json = request.getJson();

        for( FileItem item : items ) {
            try {
                if( UploadHandler.commonsUploader.isValid( item ) ) {
                    String filename = UploadHandler.commonsUploader.getUploadFilename( item );
                    UploadHandler.UploadFile uf = UploadHandler.DefaultFilenamer.getUploadDestination( request.getUser().getIdentifier().toString(), filename, core.getUploadPath() );

                    UploadHandler.commonsUploader.write( item, uf.file );

                    FileResource fr = null;
                    try {

                        json.addProperty( "title", filename );

                        FileResource.FileDescriptor d = core.getDescriptor( FileResource.class );
                        fr = d.newInstance( json, core.getRoot() );

                        fr.setPath( uf.relativePath );
                        fr.setFilename( uf.file.getName() );
                        fr.setFileExtension( uf.extension );
                        fr.setSize( UploadHandler.commonsUploader.getSize( item ) );

                        String rid = request.getValue( "rid", null );
                        if(rid != null) {
                            fr.addAssociatedResource( rid );
                            fr.addRelation( Relation.BasicRelation.CREATED_FOR, rid );
                        }

                        r.rid = fr.getIdentifier();
                        r.name = fr.getDisplayName();

                        logger.debug( "THE OWNER IS {}", fr.getOwner() );

                        // Image uploads wrapper
                        if(ImageUploadsWrapper.isImage( filename )) {
                            ImageUploadsWrapper.ImageUploadsWrapperDescriptor descriptor = core.getDescriptor( ImageUploadsWrapper.class );
                            ImageUploadsWrapper wrapper = descriptor.getWrapper( request.getUser() );
                            wrapper.addImage( fr );
                            wrapper.setUpdated(null);
                            wrapper.save();

                            fr.setVisibility( AbstractNode.Visibility.INVISIBLE );

                            //fr
                        }

                        fr.save();

                        // Fire on created node, TODO: Perhaps an onCreatedResource?
                        NodeListener.fireOnNodeCreated( core, fr );

                    } catch( ItemInstantiationException e ) {
                        logger.log( Level.ERROR, "Failed to create file resource for {}", item, e );
                        // TODO Delete uploaded file?
                    }
                }
            } catch( Exception e ) {
                logger.log( Level.ERROR, "Unable to store {}", item, e );
            }
        }


        logger.debug( "Executed!" );

        PrintWriter writer = response.getWriter();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        writer.write( gson.toJson( r ) );
    }

    public void doUploadForm(Request request, Response response) throws IOException, TemplateException, NotFoundException {
        response.setRenderType( Response.RenderType.NONE );
        response.getWriter().write( core.getTemplateManager().getRenderer( request ).renderClass( Upload.class, "index.vm" ) );
    }

    private static class UploadResponse {
        public String name;
        public String rid;
        public long size;
        public long length;
        public String url;
        public String thumbnailUrl;
        public String deleteUrl;
        public String deleteType = "DELETE";
    }

    /**
     * @param filename
     * @param pathPrefix
     * @return First is a {@link java.io.File} relative to the context path, and the second is an absolute file.
     */
    public static Tuple<File, File> generateFile( String filename, String pathPrefix, File uploadPath ) {
        Date now = new Date();
        int mid = filename.lastIndexOf( "." );
        String fname = filename;
        String ext = null;
        if( mid > -1 ) {
            ext = filename.substring( mid + 1, filename.length() );
            fname = filename.substring( 0, mid );
        }

        String strpath = pathPrefix + "/" + formatYear.format( now ) + "/" + formatMonth.format( now ) + "/" + ext;

        File path = new File( uploadPath, strpath );
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
        return core.getRoot();
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
