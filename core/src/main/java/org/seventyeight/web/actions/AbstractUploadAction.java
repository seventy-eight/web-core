package org.seventyeight.web.actions;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;

/**
 * @author cwolfgang
 */
public abstract class AbstractUploadAction implements Action {

    private Node parent;
    private MongoDocument document;

    protected AbstractUploadAction( Node parent, MongoDocument document ) {
        this.parent = parent;
        this.document = document;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @PostMethod
    public void doUpload( Request request, Response response ) throws IOException {
        response.getWriter().println( "Boom! Uploading!" );
    }

    public void onUpload() {
        /* Default implementation is a no op, for now. */
    }

    //public abstract boolean allowMultiple();
}
