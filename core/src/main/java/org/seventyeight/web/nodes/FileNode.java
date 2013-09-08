package org.seventyeight.web.nodes;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.filetype.FileType;
import org.seventyeight.web.model.*;

import java.util.List;

/**
 * @author cwolfgang
 */
public class FileNode extends UploadableNode<FileNode> {

    private static Logger logger = Logger.getLogger( FileNode.class );

    public FileNode( Node parent, MongoDocument document ) {
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

    public static FileNode getFileByFilename( Node parent, String filename ) throws ItemInstantiationException {
        List<MongoDocument> docs = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).find( new MongoDBQuery().is( "filename", filename ), 0, 1 );

        if( docs != null && !docs.isEmpty() ) {
            return new FileNode( parent, docs.get( 0 ) );
        } else {
            throw new ItemInstantiationException( "The file " + filename + " not found" );
        }
    }

    public Class<?> getFileTypeClass() {
        List<Descriptor> descriptors = Core.getInstance().getExtensionDescriptors( FileType.class );

        logger.debug( "List is " + descriptors );

        return null;
    }

    public static class FileDescriptor extends UploadableDescriptor<FileNode> {

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
