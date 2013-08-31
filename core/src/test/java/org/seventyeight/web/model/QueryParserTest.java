package org.seventyeight.web.model;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

/**
 * @author cwolfgang
 */
public class QueryParserTest {

    @Test
    public void testRegex() {
        String s = "method1:\"snade\" bla de bla method2 : hello \"world, hello\" method-3:bam";
        Pattern pattern = Pattern.compile( QueryParser.regex );
        //Pattern pattern = Pattern.compile( "(\\w+)\\s*([=<>])\\s*(\\w+)" );
        Matcher m = pattern.matcher( s );

        while( m.find() ) {
            System.out.println("------");
            for( int i = 0 ; i < m.groupCount() ; i++ ) {
                System.out.println( i + ": " + m.group( i+1 ) );
            }
        }
    }
}
