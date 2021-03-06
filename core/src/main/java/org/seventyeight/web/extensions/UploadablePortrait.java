package org.seventyeight.web.extensions;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;
import org.seventyeight.web.nodes.FileResource;

import java.util.List;

/**
 * @author cwolfgang
 */
public class UploadablePortrait extends AbstractPortrait {

    private static Logger logger = LogManager.getLogger( UploadablePortrait.class );

    public UploadablePortrait( Core core, Node parent, MongoDocument document ) {
        super( core, parent, document );
    }

    @Override
    public String getUrl() {
        try {
            return getFile().getFileUrl();
        } catch( Exception e ) {
            logger.log( Level.WARN, "Unable to deliver url for " + this, e );
            return "";
        }
    }

    @Override
    public void updateNode( JsonObject jsonData ) {
        if(jsonData != null) {
            String id = jsonData.get( "uploadedPortraitId" ).getAsString();
            document.set( "file", id );
        }
    }

    public List<MongoDocument> getAssociatedFiles() {
        MongoDBQuery query = new MongoDBQuery().is( "type", "file" ).is( "associated", getThisIdentifier() );
        logger.debug( "QUERY IS {}", query );
        List<MongoDocument> docs = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( query, 0, 10 );

        logger.debug( "------------------------------->DOCUMENTS: {}", docs );

        //return Collections.EMPTY_LIST;
        return docs;
    }

    public FileResource getFile() throws NotFoundException, ItemInstantiationException {
        return core.getNodeById( this, getFileId() );
    }

    public String getFileId() {
        return document.get("file", "");
    }

    @Override
    public String getDisplayName() {
        return "Uploadable portrait";
    }

    @Override
    public String getMainTemplate() {
        return null;  /* Implementation is a no op */
    }

    public static class UploadablePortraitDescriptor extends AbstractPortraitDescriptor {

        public UploadablePortraitDescriptor( Core core ) {
            super( core );
        }

        @Override
        public String getDisplayName() {
            return "Upload portrait";
        }

        @Override
        public String getPostConfigurationPage() {
            return "postConfig";
        }
    }
}
