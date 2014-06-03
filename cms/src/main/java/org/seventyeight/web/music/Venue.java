package org.seventyeight.web.music;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NodeDescriptor;
import org.seventyeight.web.model.Resource;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Venue extends Resource<Venue> {

    private static Logger logger = LogManager.getLogger( Venue.class );

    public Venue( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public void updateNode( CoreRequest request, JsonObject jsonData ) {
        if(jsonData != null) {
            boolean multiStaged = false;
            JsonElement e = jsonData.get( "multiStaged" );
            if(!e.isJsonNull()) {
                multiStaged = e.getAsString().equals( "on" );
            }

            document.set( "multiStaged", multiStaged );
            if(multiStaged) {
                JsonElement s = jsonData.get( "stages" );
                if(!s.isJsonNull()) {
                    logger.debug( "JSON ELEMENT: {}", s );
                    String[] stages = s.getAsString().split( "\\s*,\\s*|\\s+" );
                    document.set( "stages", stages );
                }
            }
        }
    }

    public boolean isMultiStaged() {
        return document.get( "multiStaged", false );
    }

    public void setMultiStaged(boolean multiStaged) {
        document.set( "multiStaged", multiStaged );
    }

    public void addStage(String stage) {
        document.addToList( "stages", stage );
    }

    public List<String> getStages() {
        return document.getObjectList2( "stages" );
    }

    public void doGetStage(Request request, Response response) throws IOException {
        response.setRenderType( Response.RenderType.NONE );

        String term = request.getValue( "term", "" );

        if( term.length() > 1 ) {
            PrintWriter writer = response.getWriter();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            writer.write( gson.toJson( getStageMatching( term ) ) );
        } else {
            response.getWriter().write( "{}" );
        }
    }

    public List<String> getStageMatching(String term) {
        String t = term.toLowerCase();

        List<String> r = new ArrayList<String>(  );
        for(String stage : getStages()) {
            if(stage.toLowerCase().contains( t )) {
                r.add( stage );
            }
        }

        return r;
    }

    public static class VenueDescriptor extends NodeDescriptor<Venue> {

        @Override
        public String getType() {
            return "venue";
        }

        @Override
        public String getDisplayName() {
            return "Venue";
        }
    }
}
