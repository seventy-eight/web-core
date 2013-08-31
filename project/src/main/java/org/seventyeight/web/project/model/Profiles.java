package org.seventyeight.web.project.model;

import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;

/**
 * @author cwolfgang
 */
public class Profiles implements Node {

    @PostMethod
    public void doCreate( Request request, Response response ) throws ItemInstantiationException, IOException {
        String name = request.getValue( "username", "" );
        String email = request.getValue( "email", "" );
        String firstName = request.getValue( "first", "" );
        String lastName = request.getValue( "last", "" );
        String pass1 = request.getValue( "pass1", "" );
        String pass2 = request.getValue( "pass2", "" );

        if( name.length() < 3 ) {
            throw new IllegalStateException( "The username must be more than 3 characters" );
        }

        /* Check email */

        if( firstName.length() < 2 || lastName.length() < 2 ) {
            throw new IllegalStateException( "The name must be valid" );
        }

        /* Check password */

        Profile profile = Profile.createProfile( name, firstName, lastName, email, pass1 );
        profile.save();

        response.sendRedirect( "/profile/" + profile.getUsername() + "/" );
    }

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public String getDisplayName() {
        return "Profiles";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }
}
