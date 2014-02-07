package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * @author cwolfgang
 */
public abstract class Widget extends Configurable implements ExtensionPoint, Node {

    public abstract String getDisplayName();

    public abstract String getName();

    public void doView(Request request, Response response) throws TemplateException, IOException {
        response.setRenderType( Response.RenderType.NONE );

        PrintWriter writer = response.getWriter();
        writer.write( Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( this, "view.vm" ) );
    }
}
