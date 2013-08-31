package org.seventyeight.web.unit;

import org.junit.Test;
import org.seventyeight.web.utilities.ServletUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author cwolfgang
 */
public class ServletUtilsTest {

    @Test
    public void testReplace() {
        String r = ServletUtils.replace( "file.jpg", "other" );

        assertThat( r, is( "other.jpg" ) );
    }

    @Test
    public void testReplaceMultipleDots() {
        String r = ServletUtils.replace( "file.jpg.bak", "other" );

        assertThat( r, is( "other.jpg.bak" ) );
    }

    @Test
    public void testReplaceNoExtension() {
        String r = ServletUtils.replace( "file", "other" );

        assertThat( r, is( "other" ) );
    }
}
