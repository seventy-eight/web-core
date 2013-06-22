package org.seventyeight.web;

import org.junit.ClassRule;
import org.junit.Test;
import org.seventyeight.TokenList;
import org.seventyeight.web.model.*;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.utilities.Parameters;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author cwolfgang
 */
public class CoreTest {

    @ClassRule
    public static DummyCoreEnvironment env = new DummyCoreEnvironment( "coreTest" );

    @Test
    public void test01() throws NotFoundException, UnsupportedEncodingException {

        TokenList tokens = new TokenList( "/user/wolle" );
        Node nodeItem = Core.getInstance().resolveNode( tokens );

        System.out.println( tokens );

        assertThat( nodeItem, is( (Node)env.getCore() ) );
        assertThat( tokens.left(), is( 0 ) );
    }

    @Test
    public void test02() throws NotFoundException, UnsupportedEncodingException {

        Core.getInstance().addNode( "user", new DummyNode( Core.getInstance() ) );

        TokenList tokens = new TokenList( "/user/wolle" );
        Node nodeItem = Core.getInstance().resolveNode( tokens );

        System.out.println( tokens );
    }

    public User createUser( String username ) throws ItemInstantiationException, ClassNotFoundException, SavingException {
        User user = Core.getInstance().createNode( User.class, "users" );

        Parameters p = new Parameters();
        p.put( "username", username );

        user.save( p, null );

        return user;
    }

}
