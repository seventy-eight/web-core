package org.seventyeight.web.installers;

import com.google.gson.JsonObject;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.nodes.Topic;
import org.seventyeight.web.nodes.User;

/**
 * @author cwolfgang
 */
public class ArticleInstall extends DefaultNodeInstall<Topic> {

    public ArticleInstall( String title, User owner ) {
        super( title, owner );
        this.owner = owner;
    }

    @Override
    protected void setJson( JsonObject json ) {
      /* Implementation is a no op */
    }

    @Override
    protected Descriptor<Topic> getDescriptor() {
        return Core.getInstance().getDescriptor( Topic.class );
    }

    @Override
    protected Topic getNodeFromDB() {
        return null;
    }
}
