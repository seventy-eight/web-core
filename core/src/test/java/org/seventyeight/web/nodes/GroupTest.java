package org.seventyeight.web.nodes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.DummyCoreEnvironment;
import org.seventyeight.web.WebCoreEnv;

import static com.mongodb.util.MyAsserts.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author cwolfgang
 */
public class GroupTest {

    private static Logger logger = LogManager.getLogger( GroupTest.class );

    @Rule
    public WebCoreEnv env = new DummyCoreEnvironment( "seventyeight-test-group-test" );

    @Test
    public void basicTest() throws DatabaseException {
        User u1 = env.createUser( "wolle" );

        Group group = env.createGroup( "group 1", u1 );

        group.addMember( u1 );

        assertNotNull( u1 );
        assertNotNull( group );

        assertThat( u1.getGroups().size(), is( 1 ) );
        assertThat( u1.getGroups().get( 0 ), is( group ) );
    }

    @Test
    public void basicTestAddAgain() throws DatabaseException {
        User u1 = env.createUser( "wolle" );

        Group group = env.createGroup( "group 1", u1 );

        group.addMember( u1 );
        group.addMember( u1 );

        assertNotNull( u1 );
        assertNotNull( group );

        assertThat( u1.getGroups().size(), is( 1 ) );
        assertThat( u1.getGroups().get( 0 ), is( group ) );
    }

    @Test
    public void basicTestMultipleMembers() throws DatabaseException {
        User u1 = env.createUser( "wolle" );
        User u2 = env.createUser( "bolle" );
        User u3 = env.createUser( "snolle" );

        Group group = env.createGroup( "group 1", u1 );

        group.addMember( u1 );
        group.addMember( u2 );

        assertNotNull( u1 );
        assertNotNull( group );

        assertThat( u1.getGroups().size(), is( 1 ) );
        assertThat( u2.getGroups().size(), is( 1 ) );
        assertThat( u1.getGroups().get( 0 ), is( group ) );
        assertThat( u2.getGroups().get( 0 ), is( group ) );

        assertTrue( group.isMember( u1 ) );
        assertTrue( group.isMember( u2 ) );
        assertFalse( group.isMember( u3 ) );

        assertThat( group.getMembers().size(), is( 2 ) );
    }


}
