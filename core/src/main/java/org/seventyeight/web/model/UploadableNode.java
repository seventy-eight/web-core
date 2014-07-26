package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;

import java.io.File;

/**
 * @author cwolfgang
 */
public abstract class UploadableNode<T extends UploadableNode<T>> extends Resource<T> implements Uploadable {

    public static final String FILENAME = "filename";
    public static final String EXTENSION = "ext";
    public static final String UPLOADID = "uploadID";
    public static final String EXPECTEDSIZE = "expectedSize";

    public UploadableNode( Core core, Node parent, MongoDocument document ) {
        super( core, parent, document );
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
        //return new File( getFilename() );
        File file = new File( new File( core.getUploadPath(), getPath() ), getFilename() );
        return file;
    }

    public void setFileExtension( String ext ) {
        this.document.set( EXTENSION, ext );
    }

    public String getFileExtension() {
        return this.document.get( EXTENSION );
    }

    public void addAssociatedResource(String id) {
        document.addToList( "associated", id );
    }

    public void setSize( long size ) {
        this.document.set( "size", size );
    }

    public long getSize() {
        return this.document.get( "size", 0l );
    }

    /*
    public void setExpectedFileSize( long byteSize ) {
        this.document.set( EXPECTEDSIZE, byteSize );
    }

    public long getExpectedFileSize() {
        return this.document.get( EXPECTEDSIZE );
    }
    */

    public void setPath( String path ) {
        this.document.set( "path", path );
    }

    public String getPath() {
        return this.document.get( "path" );
    }

    public static abstract class UploadableDescriptor<T1 extends UploadableNode<T1>> extends NodeDescriptor<T1> {

        protected UploadableDescriptor( Core core ) {
            super( core );
        }
    }
}
