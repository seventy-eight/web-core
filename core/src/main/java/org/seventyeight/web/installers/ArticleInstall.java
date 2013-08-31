package org.seventyeight.web.installers;

import org.seventyeight.web.Core;
import org.seventyeight.web.model.Descriptor;
import org.seventyeight.web.nodes.Article;
import org.seventyeight.web.nodes.Group;
import org.seventyeight.web.nodes.User;

/**
 * @author cwolfgang
 */
public class ArticleInstall extends DefaultNodeInstall<Article> {

    public ArticleInstall( String title, User owner ) {
        super( title, owner );
        this.owner = owner;
    }

    @Override
    protected Descriptor<Article> getDescriptor() {
        return Core.getInstance().getDescriptor( Article.class );
    }

    @Override
    protected Article getNodeFromDB() {
        return null;
    }
}
