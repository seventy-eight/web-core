package org.seventyeight.web.nodes;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.Date;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.filetype.FileType;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.utilities.ServletUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author cwolfgang
 */
public class FileResource extends UploadableNode<FileResource> {

    private static Logger logger = Logger.getLogger( FileResource.class );

    private static SimpleDateFormat formatYear = new SimpleDateFormat( "yyyy" );
    private static SimpleDateFormat formatMonth = new SimpleDateFormat( "MM" );

    public FileResource( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String getPortrait() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return getTitle();
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    public static String getUploadDestination( Request request ) {
        Date now = new Date();
        String strpath = request.getUser().getUsername() + "/" + formatYear.format( now ) + "/" + formatMonth.format( now );

        //return new File( Core.getInstance().getUploadPath(), strpath );
        return strpath;
    }

    public static FileResource create( String filename ) throws ItemInstantiationException {
        FileDescriptor descriptor = Core.getInstance().getDescriptor( FileResource.class );
        return descriptor.newInstance( filename );
    }

    public static FileResource upload( Request request, Response response ) throws Exception {
        List<FileResource> fileResources = ServletUtils.upload2( request, Core.getInstance().getUploadPath(), getUploadDestination( request ), false, 1 );
        if( fileResources.size() > 0 ) {
            return fileResources.get( 0 );
        } else {
            return null;
        }
    }

    public static FileResource getFileByFilename( Node parent, String filename ) throws ItemInstantiationException {
        List<MongoDocument> docs = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).find( new MongoDBQuery().is( "filename", filename ), 0, 1 );

        if( docs != null && !docs.isEmpty() ) {
            return new FileResource( parent, docs.get( 0 ) );
        } else {
            throw new ItemInstantiationException( "The file " + filename + " not found" );
        }
    }

    public Class<?> getFileTypeClass() {
        List<Descriptor> descriptors = Core.getInstance().getExtensionDescriptors( FileType.class );

        logger.debug( "List is " + descriptors );

        return null;
    }

    public static class FileDescriptor extends UploadableDescriptor<FileResource> {

        @Override
        public String getType() {
            return "file";
        }

        @Override
        public String getDisplayName() {
            return "File";
        }

        @Override
        public Node getChild( String name ) throws NotFoundException {
            try {
                return getFileByFilename( this, name );
            } catch( ItemInstantiationException e ) {
                logger.debug( e );
            }

            throw new NotFoundException( "The file " + name + " was not found" );
        }
    }
}
