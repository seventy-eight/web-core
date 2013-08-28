package org.seventyeight.web.servlet;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author cwolfgang
 */
public class ResponseTest {

    @Test
    public void testMimeType() throws IOException {
        String filename = "C:\\Temp\\html\\css.css";
        File file = new File( filename );
        assertTrue( file.exists() );
        String contentType = Response.getMimeType2( file );

        System.out.println( contentType );
        System.out.println( file.getName() );
    }
}
