package org.seventyeight.web.nodes;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

import java.util.List;

/**
 * @author cwolfgang
 */
public class FileNode extends Entity {

    private static Logger logger = Logger.getLogger( FileNode.class );

    public FileNode( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    public void setUploadIdentity( String uid ) {
        document.set( "uploadID", uid );
    }

    public String getUploadIdentity() {
        return document.get( "uploadID" );
    }

    public void setFilename( String filename ) {
        document.set( "filename", filename );
    }

    public String getFilename() {
        return document.get( "filename" );
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
        List<MongoDocument> docs = MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).find( new MongoDBQuery().is( "filename", filename ), 0, 1 );

        if( docs != null && !docs.isEmpty() ) {
            return new FileNode( parent, docs.get( 0 ) );
        } else {
            throw new ItemInstantiationException( "The file " + filename + " not found" );
        }
    }

    public static class FileDescriptor extends NodeDescriptor<FileNode> {

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
