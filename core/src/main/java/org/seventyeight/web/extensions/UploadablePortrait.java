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
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
      /* Implementation is a no op */
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
