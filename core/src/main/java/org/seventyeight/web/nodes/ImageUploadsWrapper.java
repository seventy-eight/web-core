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

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author cwolfgang
 */
public class ImageUploadsWrapper extends AbstractNode<ImageUploadsWrapper> {

    private static Logger logger = LogManager.getLogger( ImageUploadsWrapper.class );

    public static final String TITLE = "imageuploadswrapper";

    protected static final Pattern pattern = Pattern.compile( "\\.png", Pattern.CASE_INSENSITIVE );

    public ImageUploadsWrapper( Core core, Node parent, MongoDocument document ) {
        super( core, parent, document );
    }

    public void addImage(FileResource file) {
        if(isImage( file.getFilename() )) {
            document.addToList( "images", file.getIdentifier() );
        }
    }

    public List<String> getImageIds() {
        MongoDBQuery query = new MongoDBQuery().is("uploadSession", this.getUploadSession()).is("type", "file");
        MongoDocument sort = new MongoDocument().set("created", 1);
        
        return core.getIds(query, 0, 0, sort);
    }
    
    public String getUploadSession() {
    	return document.get("uploadSession");
    }

    @Override
    public void updateNode( JsonObject jsonData ) {
    	/* Implementation is a no op */
    }

    public static boolean isImage(String filename) {
        return filename.matches( ".+\\.(?i:png|jpg|jpeg)$" );
    }

    public static class ImageUploadsWrapperDescriptor extends NodeDescriptor<ImageUploadsWrapper> {
    	
    	private ConcurrentHashMap<String, String> uploadSessions;

        public ImageUploadsWrapperDescriptor( Node parent ) {
            super( parent );
            logger.debug("PARENTNTT: {}", parent);
            
            uploadSessions = new ConcurrentHashMap<String, String>();
        }

        @Override
        public String getType() {
            return TITLE;
        }
        
        @Override
		public String getUrlName() {
			return "uploadWrapper";
		}

        @Override
        public String getDisplayName() {
            return "Image uploads wrapper";
        }
        
        @Override
		public void initialize() {
			uploadSessions = new ConcurrentHashMap<String, String>();
		}

		private synchronized boolean doSessionUploadThingy(User user, String uploadSession) {
        	String us = uploadSessions.get(user.getIdentifier());
        	if(us != null && us.equals(uploadSession)) {
        		return false;
        	} else {
        		uploadSessions.put(user.getIdentifier(), uploadSession);
        		return true;
        	}
        }
        
        public MongoDocument getWrapperDocument(User user, String uploadSession) {
        	MongoDBQuery query = new MongoDBQuery().is( "type", TITLE ).is( "owner", user.getIdentifier() ).is("uploadSession", uploadSession);
            MongoDocument sort = new MongoDocument().set( "created", -1 );
            MongoDocument doc = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).findOne( query, null, sort );
            logger.debug( "IMAGE UPLOADS WRAPPER DOCUMENT: {}", doc );
            
            return doc;
        }

        public ImageUploadsWrapper getWrapper( Core core, User user, String uploadSession) throws ItemInstantiationException {
        	/*
            MongoDBQuery query = new MongoDBQuery().is( "type", TITLE ).is( "owner", user.getIdentifier() ).is("uploadSession", uploadSession);
            MongoDocument sort = new MongoDocument().set( "created", -1 );
            MongoDocument doc = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).findOne( query, null, sort );
            logger.debug( "IMAGE UPLOADS WRAPPER DOCUMENT: {}", doc );
            if(doc == null || doc.isNull()) {
            */
        	JsonObject json = new JsonObject();
        	json.addProperty("title", "Wrapper for " + user.getDisplayName());
        	
        	if(doSessionUploadThingy(user, uploadSession)) {
                ImageUploadsWrapper instance = newInstance( core, this, json, user.getIdentifier() );
                instance.setOwner( user );
                instance.getDocument().set("uploadSession", uploadSession);
                return instance;
            } else {
            	MongoDocument doc = getWrapperDocument(user, uploadSession);
                return core.getNode( this, doc );
            }
        }
    }
}
