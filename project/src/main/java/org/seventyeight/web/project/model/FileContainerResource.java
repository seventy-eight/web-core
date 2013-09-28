package org.seventyeight.web.project.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.Resource;
import org.seventyeight.web.nodes.FileResource;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import javax.servlet.http.HttpServletResponse;

/**
 * @author cwolfgang
 */
public abstract class FileContainerResource<T extends Resource<T>> extends Resource<T> {

    public static final String FILE_ATTACHMENTS_ELEMENT = "attachments";

    public FileContainerResource( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    public void doAttachFile( Request request, Response response ) {
        try {
            FileResource fr = FileResource.upload( request, response );
            addFile( fr );
            this.save();

            response.setStatus( HttpServletResponse.SC_OK );
        } catch( Exception e ) {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
        }
    }

    public void addFile( FileResource fileResource ) {
        document.addToList( FILE_ATTACHMENTS_ELEMENT, fileResource.getIdentifier() );
    }
}
