package org.seventyeight.web.functional.actiontest;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.DummyCoreEnvironment;
import org.seventyeight.web.Root;
import org.seventyeight.web.model.NotFoundException;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author cwolfgang
 */
public class ActionTest {

    @ClassRule
    public static DummyCoreEnvironment env = new DummyCoreEnvironment( new Root(), "seventyeight-action-test" );

    @Test
    public void test() throws NotFoundException {
        MongoDocument document = new MongoDocument();
        MyNode mn = new MyNode( env.getCore(), env.getRoot(), document );
        mn.setValue( "some", "thing" );

        MyAction mya = (MyAction) mn.getChild( "snade" );
        mya.setValue( "test", "1" );

        assertNotNull( mya );
        assertThat( mya.getValue( "test" ), is( "1" ) );

        System.out.println( "NODE: " + mn.getDocument() );

        MyAction mya2 = (MyAction) mn.getChild( "snade" );
        mya2.setValue( "test", "2" );

        assertNotNull( mya2 );

        System.out.println( "NODE: " + mn.getDocument() );

        assertThat( mya2.getValue( "test" ), is( "2" ) );
    }
}
