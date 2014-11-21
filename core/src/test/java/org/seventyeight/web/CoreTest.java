package org.seventyeight.web;

import org.junit.ClassRule;
import org.junit.Test;
import org.seventyeight.TokenList;
import org.seventyeight.web.authentication.NoAuthorizationException;
import org.seventyeight.web.model.*;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.utilities.SimpleContext;

import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author cwolfgang
 */
public class CoreTest {

    @ClassRule
    public static DummyCoreEnvironment env = new DummyCoreEnvironment( new Root(), "coreTest" );

    @Test
    public void test01() throws NotFoundException, UnsupportedEncodingException, ItemInstantiationException, NoAuthorizationException {

        TokenList tokens = new TokenList( "/user/wolle" );
        Node nodeItem = env.getCore().resolveNode( tokens, null );

        System.out.println( tokens );

        assertThat( nodeItem, is( (Node)env.getCore() ) );
        assertThat( tokens.left(), is( 0 ) );
    }

    @Test
    public void test02() throws NotFoundException, UnsupportedEncodingException, ItemInstantiationException, NoAuthorizationException {

        env.getCore().getRoot().addNode( "user", new DummyNode( env.getCore().getRoot() ) );

        TokenList tokens = new TokenList( "/user/wolle" );
        Node nodeItem = env.getCore().resolveNode( tokens, null );

        System.out.println( tokens );
    }

    public User createUser( String username ) throws ItemInstantiationException, ClassNotFoundException, SavingException {
        //User user = Core.getInstance().createNode( User.class, "users" );
        User.UserDescriptor d = env.getCore().getDescriptor( User.class );
        JsonObject json = new JsonObject();
        json.addProperty("title", "owner-0");
        User user = d.newInstance( env.getCore(), env.getCore().getRoot(), json, username );

        SimpleContext p = new SimpleContext( env.getCore() );
        //p.put( "username", username );
        p.getJson().addProperty("username", username);

        user.updateNode( null );

        user.save();

        return user;
    }

}
