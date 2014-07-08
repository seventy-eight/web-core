package org.seventyeight.web.nodes;

import org.junit.Test;

import static com.mongodb.util.MyAsserts.assertFalse;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author cwolfgang
 */
public class ImageUploadsWrapperTest {

    @Test
    public void testIsImage() {
        assertTrue(ImageUploadsWrapper.isImage( "snade.png" ));
        assertTrue(ImageUploadsWrapper.isImage( "snade.PNG" ));
        assertTrue(ImageUploadsWrapper.isImage( "snade.jpg" ));
        assertTrue(ImageUploadsWrapper.isImage( "snade.JPEG" ));

        assertFalse( ImageUploadsWrapper.isImage( "snade.txt" ) );
        assertFalse( ImageUploadsWrapper.isImage( ".JPG" ) );
    }
}
