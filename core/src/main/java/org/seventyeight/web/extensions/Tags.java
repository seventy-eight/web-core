package org.seventyeight.web.extensions;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.Node;

/**
 * @author cwolfgang
 */
public class Tags extends ResourceExtension<Tags> {
    public Tags( Node node, MongoDocument document ) {
        super( node, document );
    }

    @Override
    public String getDisplayName() {
        return "Tags";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    @Override
    public void updateNode( CoreRequest request, JsonObject jsonData ) {
      /* Implementation is a no op */
    }

    public static class TagsDescriptor extends ExtensionDescriptor<Tags> {

        @Override
        public String getDisplayName() {
            return "Tags";
        }

        @Override
        public String getExtensionName() {
            return "tags";
        }

        @Override
        public String getTypeName() {
            return "tags";
        }
    }
}
