package org.seventyeight.web.project.actions;

import org.seventyeight.web.Core;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.model.SearchAction;
import org.seventyeight.web.project.model.Certificate;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;

/**
 * @author cwolfgang
 */
public class CertificateSearch extends SearchAction {

    public CertificateSearch( Node parent ) {
        super( parent );
    }

    public void doIndex( Request request, Response response ) throws IOException {
        response.getWriter().println( "BOOM!" );
    }

    @Override
    public String getUrlName() {
        return Certificate.CERTIFICATE_STRING;
    }

    @Override
    public Node getChild( String name ) throws NotFoundException {
        return null;
    }

    @Override
    public String getDisplayName() {
        return Certificate.CERTIFICATE_NAME + " search";
    }

    @Override
    public String getMainTemplate() {
        return Core.MAIN_TEMPLATE;
    }
}
