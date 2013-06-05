package org.seventyeight.utils;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author cwolfgang
 */
public class EncodingUtilsTest {

    @Test
    public void test() throws UnsupportedEncodingException {
        String s = URLEncoder.encode( "rød", "UTF-8" );
        assertThat( s, is( "r%C3%B8d" ) );
    }

    @Test
    public void testDecode() throws UnsupportedEncodingException {
        String s = URLDecoder.decode( "r%C3%B8d", "UTF-8" );
        System.out.println( "--->" + s );
        assertThat( s, is( "rød" ) );
    }
}
