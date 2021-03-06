package org.seventyeight.web.extensions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.AbstractExtension;
import org.seventyeight.web.model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cwolfgang
 */
public class Tags extends AbstractExtension<Tags> {

    private static Logger logger = LogManager.getLogger( Tags.class );

    public Tags( Core core, Node node, MongoDocument document ) {
        super( core, node, document );
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
    public void updateNode( JsonObject jsonData ) {
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

                //((AbstractNode<?>)parent).getDocument().set( "tags", tagList );
                document.set( "tags", tagList );
                //document.setList( ( )
            }
        }
    }

    public List<Object> getTags() {
        return document.getObjectList( "tags" );
    }

    public static final class TagsDescriptor extends ExtensionDescriptor<Tags> {

        public TagsDescriptor( Core core ) {
            super();
        }

        @Override
        public String getDisplayName() {
            return "Tags";
        }

        @Override
        public ExtensionGroup getExtensionGroup() {
            return new ExtensionGroup( getClazz(), "Tags" );
        }

        @Override
        public Class<Tags> getExtensionClass() {
            return Tags.class;
        }

        @Override
        public Map<String, String> getSearchKeyMap() {
            Map<String, String> m = new HashMap<String, String>( 1 );
            m.put( "tags", "" );
            return m;
        }
    }
}
