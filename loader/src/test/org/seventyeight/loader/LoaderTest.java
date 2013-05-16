package org.seventyeight.loader;

import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * @author cwolfgang
 */
public class LoaderTest {

    @Test
    public void test() {
        ClassLoader classLoader = new ClassLoader( Thread.currentThread().getContextClassLoader() );

        Loader loader = new Loader( classLoader );

        File path = new File( "target/classes" );
        System.out.println( "----> " + new File( "target/classes" ).getAbsolutePath() );

        List<String> s = loader.getClasses( path, "" );

        System.out.println( "S: " + s );
    }
}
