package org.seventyeight.web.social;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.extensions.ExtensionGroup;
import org.seventyeight.web.extensions.NodeExtension;
import org.seventyeight.web.model.AbstractExtension;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.nodes.User;

/**
 * @author cwolfgang
 */
public class Follow extends NodeExtension<Follow> {

    private static Logger logger = LogManager.getLogger( Follow.class );

    public Follow( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String getDisplayName() {
        return "Follow";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    @Override
    public void updateNode( CoreRequest request, JsonObject jsonData ) {
        /* Implementation is a no op */
    }

    public static class FollowDescriptor extends NodeExtensionDescriptor<Follow> {

        @Override
        public boolean isApplicable( Node node ) {
            logger.fatal( "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIS APPLICABLE: {}", node );
            return node instanceof User;
        }

        @Override
        public String getDisplayName() {
            return "Follow";
        }

        @Override
        public String getExtensionName() {
            return "follow";
        }

        @Override
        public String getTypeName() {
            return "follow";
        }
    }
}
