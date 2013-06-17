package org.seventyeight.web.actions;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.utilities.ServletUtils;

import java.io.File;
import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class AbstractUploadAction implements Action {

    private static Logger logger = Logger.getLogger( AbstractUploadAction.class );

    private Node node;
    private MongoDocument document;

    protected AbstractUploadAction( Node node, MongoDocument document ) {
        this.node = node;
        this.document = document;
    }

    public Node getNode() {
        return node;
    }

    public abstract File getPath();

    @PostMethod
    public void doUpload( Request request, Response response ) throws Exception {
        logger.debug( "Uploading file" );

        String relativePath = request.getUser().getIdentifier();
        File path = new File( getPath(), relativePath );

        if( !path.exists() && !path.mkdirs() ) {
            throw new IllegalStateException( "Unable to create path " + path.toString() );
        }

        List<String> uploadedFilenames = ServletUtils.upload( request, path, true, 1 );

        logger.debug( "Filenames: " + uploadedFilenames );

        if( uploadedFilenames.size() > 0 ) {
            setFile( new File( relativePath, uploadedFilenames.get( 0 ) ).toString() );
        } else {
            throw new IllegalStateException( "No file uploaded" );
        }

        response.sendRedirect( "" );
    }

    public void setFile( String file ) {
        document.set( "file", file );
    }

    public void onUpload() {
        /* Default implementation is a no op, for now. */
    }

    //public abstract boolean allowMultiple();
}
