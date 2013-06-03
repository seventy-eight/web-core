package org.seventyeight.web;

import org.junit.ClassRule;
import org.junit.Test;
import org.seventyeight.web.extensions.footer.Footer;
import org.seventyeight.web.model.*;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.nodes.Users;
import org.seventyeight.web.utilities.Parameters;

import java.io.IOException;
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
    public void test01() throws NotFoundException {

        List<String> tokens = new ArrayList<String>();
        Node nodeItem = Core.getInstance().resolveNode( "/user/wolle", tokens );

        System.out.println( tokens );

        assertThat( nodeItem, is( (Node)env.getCore() ) );
        assertThat( tokens.size(), is( 2 ) );
        assertThat( tokens.get( 0 ), is( "user" ) );
        assertThat( tokens.get( 1 ), is( "wolle" ) );
    }

    @Test
    public void test02() throws NotFoundException {

        Core.getInstance().addNode( "user", new DummyNode( Core.getInstance() ) );

        LinkedList<String> tokens = new LinkedList<String>();
        Node nodeItem = Core.getInstance().resolveNode( "/user/wolle", tokens );

        System.out.println( tokens );
    }

    @Test
    public void test03() throws ClassNotFoundException, SavingException, ItemInstantiationException, NoSuchMethodException, NotFoundException {

        Users users = new Users( Core.getInstance() );
        Core.getInstance().addNode( "user", users );
        User user = createUser( "wolle" );

        LinkedList<String> tokens = new LinkedList<String>();
        Node nodeItem = Core.getInstance().resolveNode( "/user/wolle", tokens );

        assertThat( (User) nodeItem, is( user ) );

        System.out.println( tokens );
    }

    @Test
    public void test04() throws ClassNotFoundException, SavingException, ItemInstantiationException, NoSuchMethodException, NotFoundException {

        Users users = new Users( Core.getInstance() );
        Core.getInstance().addNode( "user", users );
        createUser( "wolle" );

        LinkedList<String> tokens = new LinkedList<String>();
        Node nodeItem = Core.getInstance().resolveNode( "/user/wolle", tokens );

        assertThat( tokens.size(), is( 1 ) );
        assertThat( tokens.get( 0 ), is( "wolle" ) );
    }


    public User createUser( String username ) throws ItemInstantiationException, ClassNotFoundException, SavingException {
        User user = Core.getInstance().createNode( User.class, "users" );

        Parameters p = new Parameters();
        p.put( "username", username );

        user.save( p, null );

        return user;
    }

}
