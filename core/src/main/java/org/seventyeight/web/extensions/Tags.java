package org.seventyeight.web.extensions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.AbstractExtension;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Tags extends ResourceExtension<Tags> {

    private static Logger logger = LogManager.getLogger( Tags.class );

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
        logger.debug( "Updating tags, {}", jsonData );

        if(jsonData != null) {
            JsonArray tags = jsonData.getAsJsonArray( "tags" );
            logger.debug( "TAGS: {}", tags );
            if(tags != null && tags.size() > 0) {
                List<String> tagList = new ArrayList<String>( tags.size() );
                for( JsonElement t : tags) {
                    tagList.add( t.getAsString() );
                }

                logger.debug( "TAG LIST: {}", tagList );
                document.set("tags", tagList);
                //document.setList( ( )
            }
        }
    }

    public List<Object> getTags() {
        return document.getObjectList( "tags" );
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

        @Override
        public ExtensionGroup getExtensionGroup() {
            return new ExtensionGroup( getClazz(), "Tags" );
        }

        @Override
        public Class<Tags> getExtensionClass() {
            return Tags.class;
        }
    }
}
