package org.seventyeight.utils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author cwolfgang
 */
public class EncodingUtils {

    private static String DEFAULT_ENCODING = "UTF-8";

    public static String decode( String s ) {
        return decode( s, DEFAULT_ENCODING );
    }

    public static String decode( String s, String encoding ) {
        int i = s.indexOf( '%' );
        if( i < 0 ) return s;

        try {
            // to properly handle non-ASCII characters, decoded bytes need to be stored and translated in bulk.
            // this complex set up is necessary for us to work gracefully if 's' already contains decoded non-ASCII chars.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            StringBuilder buf = new StringBuilder( s.substring( 0, i ) );
            char c, upper, lower;
            for( int m = s.length(); i < m; i++ ) {
                c = s.charAt( i );
                if( c == '%' ) {
                    try {
                        upper = s.charAt( ++i );
                        lower = s.charAt( ++i );
                        baos.write( fromHex( upper ) * 16 + fromHex( lower ) );
                    } catch( IndexOutOfBoundsException ignore ) {
                        // malformed %HH.
                    }
                } else {
                    if( baos.size() > 0 ) {
                        buf.append( new String( baos.toByteArray(), encoding ) );
                        baos.reset();
                    }
                    buf.append( c );
                }
            }
            if( baos.size() > 0 ) {
                buf.append( new String( baos.toByteArray(), encoding ) );
            }
            return buf.toString();
        } catch( UnsupportedEncodingException e ) {
            throw new AssertionError( e );
        }
    }

    private static int fromHex( char upper ) {
        return ( ( upper & 0xF ) + ( ( upper & 0x40 ) != 0 ? 9 : 0 ) );
    }
}
