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

    public ArticleInstall( Core core, String title, User owner ) {
        super( core, title, owner );
        this.owner = owner;
    }

    @Override
    protected void setJson( JsonObject json ) {
      /* Implementation is a no op */
    }

    @Override
    protected Descriptor<Topic> getDescriptor() {
        return core.getDescriptor( Topic.class );
    }

    @Override
    protected Topic getNodeFromDB() {
        return null;
    }
}
