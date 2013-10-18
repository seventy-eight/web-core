package org.seventyeight.web.model;

import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;

/**
 * @author cwolfgang
 */
public abstract class ResourceFactory<R extends Resource<R>> implements Node {

    @PostMethod
    public void doCreate( Request request, Response response ) throws ItemInstantiationException, IOException {
        R instance = create( request );
        instance.save();
        response.sendRedirect( instance.getUrl() );
    }

    protected abstract R create( Request request ) throws ItemInstantiationException;
}
