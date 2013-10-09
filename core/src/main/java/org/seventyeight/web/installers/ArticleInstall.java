package org.seventyeight.web.installers;

import org.seventyeight.web.Core;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.nodes.Post;
import org.seventyeight.web.nodes.User;

/**
 * @author cwolfgang
 */
public class ArticleInstall extends DefaultNodeInstall<Post> {

    public ArticleInstall( String title, User owner ) {
        super( title, owner );
        this.owner = owner;
    }

    @Override
    protected Descriptor<Post> getDescriptor() {
        return Core.getInstance().getDescriptor( Post.class );
    }

    @Override
    protected Post getNodeFromDB() {
        return null;
    }
}
