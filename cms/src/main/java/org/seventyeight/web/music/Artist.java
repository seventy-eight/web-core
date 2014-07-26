package org.seventyeight.web.music;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NodeDescriptor;
import org.seventyeight.web.model.Resource;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author cwolfgang
 */
public class Artist extends Resource<Artist> {
    public Artist( Core core, Node parent, MongoDocument document ) {
        super( core, parent, document );
    }

    @Override
    public void updateNode( JsonObject jsonData ) {

    }

    public static final class ArtistDescriptor extends NodeDescriptor<Artist> {

        public ArtistDescriptor( Core core ) {
            super( core );
        }

        public void doGetArtists(Request request, Response response) throws IOException {
            response.setRenderType( Response.RenderType.NONE );

            String term = request.getValue( "term", "" );

            if( term.length() > 1 ) {
                MongoDBQuery query = new MongoDBQuery().is( "type", "artist" ).regex( "title", "(?i)" + term + ".*" );

                PrintWriter writer = response.getWriter();
                writer.print( MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( query, 0, 10 ) );
            } else {
                response.getWriter().write( "{}" );
            }
        }

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
