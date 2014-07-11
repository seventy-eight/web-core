package org.seventyeight.web.nodes;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.AbstractNode;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NodeDescriptor;

import java.util.regex.Pattern;

/**
 * @author cwolfgang
 */
public class ImageUploadsWrapper extends AbstractNode<ImageUploadsWrapper> {

    private static Logger logger = LogManager.getLogger( ImageUploadsWrapper.class );

    public static final String TITLE = "imageuploadswrapper";

    protected static final Pattern pattern = Pattern.compile( "\\.png", Pattern.CASE_INSENSITIVE );

    public ImageUploadsWrapper( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    public void addImage(FileResource file) {
        if(isImage( file.getFilename() )) {
            document.addToList( "images", file.getIdentifier() );
        }
    }

    @Override
    public void updateNode( JsonObject jsonData ) {
      /* Implementation is a no op */
    }

    public static boolean isImage(String filename) {
        return filename.matches( ".+\\.(?i:png|jpg|jpeg)$" );
    }

    public static class ImageUploadsWrapperDescriptor extends NodeDescriptor<ImageUploadsWrapper> {

        @Override
        public String getType() {
            return TITLE;
        }

        @Override
        public String getDisplayName() {
            return "Image uploads wrapper";
        }

        public ImageUploadsWrapper getWrapper(User user) throws ItemInstantiationException {
            MongoDBQuery query = new MongoDBQuery().is( "type", TITLE ).is( "owner", user.getIdentifier() );
            MongoDocument doc = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).findOne( query );
            logger.debug( "IMAGE UPLOADS WRAPPER DOCUMENT: {}", doc );
            if(doc == null || doc.isNull()) {
                ImageUploadsWrapper instance = newInstance( user.getIdentifier(), this, "Wrapper for " + user.getDisplayName() );
                instance.setOwner( user );
                return instance;
            } else {
                return Core.getInstance().getNode( this, doc );
            }
        }
    }
}
