package org.seventyeight.web.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.NodeExtension;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class Entity<T extends Entity<T>> extends AbstractNode<T> implements CreatableNode, Portraitable, Parent {

    private static Logger logger = Logger.getLogger( Entity.class );

    public Entity( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public List<AbstractExtension> getExtensions() {
        List<AbstractExtension> es = super.getExtensions( NodeExtension.class );
        //es.addAll( Core.getInstance().getExtensions( PermanentExtension.class ) );
        return es;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public Node getChild( String name ) {
        return null;
    }

    @Override
    public String getPortrait() {
        if( false ) {
            return "/theme/framed-question-mark-small.png";
        } else {
            return "/theme/framed-question-mark-small.png";
        }
    }

    public void doSmall( Request request, Response response ) throws TemplateException, IOException {
        response.getWriter().write( Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( this, "small.vm" ) );
    }

}
