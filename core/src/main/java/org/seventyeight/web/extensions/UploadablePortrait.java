package org.seventyeight.web.extensions;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.SavingException;

/**
 * @author cwolfgang
 */
public class UploadablePortrait extends UserPortrait {

    public UploadablePortrait( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String getUrl() {
        return "test";
    }

    @Override
    public void updateNode( CoreRequest request ) {
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

    public static class UploadablePortraitDescriptor extends UserPortraitDescriptor {

        @Override
        public String getDisplayName() {
            return "Upload portrait";
        }

        @Override
        public String getExtensionName() {
            return "uploadable-portrait";
        }
    }
}
