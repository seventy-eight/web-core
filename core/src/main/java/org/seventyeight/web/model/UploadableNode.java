package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;

import java.io.File;

/**
 * @author cwolfgang
 */
public abstract class UploadableNode<T extends UploadableNode<T>> extends Entity<T> implements Uploadable {

    public static final String FILENAME = "filename";
    public static final String EXTENSION = "ext";
    public static final String UPLOADID = "uploadID";

    public UploadableNode( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    public void setUploadIdentity( String uid ) {
        document.set( UPLOADID, uid );
    }

    public String getUploadIdentity() {
        return document.get( UPLOADID );
    }

    public void setFilename( String filename ) {
        document.set( FILENAME, filename );
    }

    public String getFilename() {
        return document.get( FILENAME );
    }

    public File getFile() {
        return new File( getFilename() );
    }

    public void setFileExtension( String ext ) {
        this.document.set( EXTENSION, ext );
    }

    public String getFileExtension() {
        return this.document.get( EXTENSION );
    }

    public static abstract class UploadableDescriptor<T1 extends UploadableNode<T1>> extends NodeDescriptor<T1> {

    }
}
