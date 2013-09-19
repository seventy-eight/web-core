package org.seventyeight.web.nodes;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Resource;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.ResourceDescriptor;
import org.seventyeight.web.model.NotFoundException;

/**
 * @author cwolfgang
 */
public class Article extends Resource<Article> {

    public Article( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String getDisplayName() {
        return getTitle();
    }

    @Override
    public String getMainTemplate() {
        return Core.MAIN_TEMPLATE;
    }

    @Override
    public String getPortrait() {
        return null;
    }

    public static class ArticleDescriptor extends ResourceDescriptor<Article> {

        @Override
        public String getType() {
            return "article";
        }

        @Override
        public Node getChild( String name ) throws NotFoundException {
            return null;
        }

        @Override
        public String getDisplayName() {
            return "Article";
        }
    }
}
