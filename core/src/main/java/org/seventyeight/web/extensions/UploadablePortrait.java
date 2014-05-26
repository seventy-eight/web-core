package org.seventyeight.web.extensions;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class UploadablePortrait extends AbstractPortrait {

    public UploadablePortrait( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String getUrl() {
        return "test";
    }

    @Override
    public void updateNode( CoreRequest request, JsonObject jsonData ) {
        /* Implementation is a no op */
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

        @Override
        public String getDisplayName() {
            return "Upload portrait";
        }

        @Override
        public String getExtensionName() {
            return "uploadable-portrait";
        }

        @Override
        public String getPostConfigurationPage() {
            return "postConfig";
        }
    }
}
