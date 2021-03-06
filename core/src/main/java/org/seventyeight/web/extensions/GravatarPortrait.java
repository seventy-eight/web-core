package org.seventyeight.web.extensions;

import com.google.gson.JsonObject;
import com.timgroup.jgravatar.Gravatar;
import com.timgroup.jgravatar.GravatarDefaultImage;
import com.timgroup.jgravatar.GravatarRating;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.nodes.User;

import java.io.File;

/**
 * @author cwolfgang
 */
public class GravatarPortrait extends AbstractPortrait {

    public GravatarPortrait( Core core, Node parent, MongoDocument document ) {
        super( core, parent, document );
    }

    @Override
    public String getUrl() {
        File path = getPortraitPath();
        Gravatar gravatar = new Gravatar( 80, GravatarRating.XPLICIT, GravatarDefaultImage.MONSTERID );
        String url = gravatar.getUrl( getEmail() );
        return url;
    }

    @Override
    public MongoDocument getDocument() {
        return document;
    }

    public String getEmail() {
        return document.get( "email" );
    }

    @Override
    public void updateNode( JsonObject jsonData ) {
        //String email = request.getValue( "gravatarEmail", null );
        String email = jsonData.getAsJsonPrimitive( "gravatarEmail" ).getAsString();
        if( email != null ) {
            document.set( "email", email );
        } else {
            throw new IllegalArgumentException( "Gravatar email not set" );
        }
    }

    @Override
    public String getDisplayName() {
        return "Gravatar";
    }

    @Override
    public String getMainTemplate() {
        return null;  /* Implementation is a no op */
    }

    public static class GravatarPortraitDescriptor extends AbstractPortraitDescriptor {

        public GravatarPortraitDescriptor( Core core ) {
            super( core );
        }

        @Override
        public String getDisplayName() {
            return "Gravatar";
        }

        @Override
        public boolean isApplicable( Node node ) {
            return node.getClass().isInstance( User.class );
        }
    }
}
