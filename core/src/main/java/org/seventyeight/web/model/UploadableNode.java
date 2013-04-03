package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;

/**
 * @author cwolfgang
 */
public abstract class UploadableNode extends Entity implements Uploadable {

    public UploadableNode( Node parent, MongoDocument document ) {
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
    public UploadableDescriptor getDescriptor() {
        return Core.getInstance().getDescriptor( getClass() );
    }
}
