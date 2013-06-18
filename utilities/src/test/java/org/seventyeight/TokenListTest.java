package org.seventyeight;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author cwolfgang
 */
public class TokenListTest {

    @Test
    public void test() {
        TokenList l = new TokenList( "1/2" );

        assertThat( l.left(), is( 2 ) );
        assertTrue( l.hasMore() );
        assertFalse( l.isEmpty() );

        String t1 = l.next();

        assertThat( t1, is( "1" ) );
        assertThat( l.left(), is( 1 ) );
        assertTrue( l.hasMore() );
        assertFalse( l.isEmpty() );

        String t2 = l.next();

        assertThat( t2, is( "2" ) );
        assertThat( l.left(), is( 0 ) );
        assertFalse( l.hasMore() );
        assertTrue( l.isEmpty() );
    }

    @Test
    public void test2() {
        TokenList l = new TokenList( "/1/2" );

        assertThat( l.left(), is( 2 ) );
        assertTrue( l.hasMore() );
        assertFalse( l.isEmpty() );

        String t1 = l.next();

        assertThat( t1, is( "1" ) );
        assertThat( l.left(), is( 1 ) );
        assertTrue( l.hasMore() );
        assertFalse( l.isEmpty() );

        String t2 = l.next();

        assertThat( t2, is( "2" ) );
        assertThat( l.left(), is( 0 ) );
        assertFalse( l.hasMore() );
        assertTrue( l.isEmpty() );
    }

    @Test
    public void test3() {
        TokenList l = new TokenList( "1/2/" );

        assertThat( l.left(), is( 2 ) );
        assertTrue( l.hasMore() );
        assertFalse( l.isEmpty() );

        String t1 = l.next();

        assertThat( t1, is( "1" ) );
        assertThat( l.left(), is( 1 ) );
        assertTrue( l.hasMore() );
        assertFalse( l.isEmpty() );

        String t2 = l.next();

        assertThat( t2, is( "2" ) );
        assertThat( l.left(), is( 0 ) );
        assertFalse( l.hasMore() );
        assertTrue( l.isEmpty() );
    }
}
