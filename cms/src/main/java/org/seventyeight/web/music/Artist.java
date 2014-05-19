package org.seventyeight.web.music;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NodeDescriptor;
import org.seventyeight.web.model.Resource;

/**
 * @author cwolfgang
 */
public class Artist extends Resource<Artist> {
    public Artist( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public void updateNode( CoreRequest request, JsonObject jsonData ) {
      /* Implementation is a no op */
    }

    public static final class ArtistDescriptor extends NodeDescriptor<Artist> {

        @Override
        public String getType() {
            return "artist";
        }

        @Override
        public String getDisplayName() {
            return "Artist";
        }
    }
}
