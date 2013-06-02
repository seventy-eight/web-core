package org.seventyeight.web.nodes;

import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.WebCoreEnv;
import org.seventyeight.web.installers.UserInstall;
import org.seventyeight.web.model.ItemInstantiationException;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author cwolfgang
 */
public class GroupTest {

    private static Logger logger = Logger.getLogger( GroupTest.class );

    @Rule
    public WebCoreEnv env = new WebCoreEnv( "seventyeight-test-group-test" );

    @Test
    public void test() throws DatabaseException {
        User u1 = env.createUser( "wolle" );
        u1.save();

        Group group = env.createGroup( "group 1", u1 );

        group.addMember( u1 );

        logger.info( "WOLLE: " + u1.getDocument() );

        assertNotNull( u1 );
        assertNotNull( group );

        assertThat( u1.getGroups().size(), is( 1 ) );
        assertThat( u1.getGroups().get( 0 ), is( group ) );
    }
}
