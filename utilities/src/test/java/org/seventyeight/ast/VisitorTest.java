package org.seventyeight.ast;

import org.junit.Test;

/**
 * @author cwolfgang
 */
public class VisitorTest {
    @Test
    public void test01() {
        Identifier i1 = new Identifier( "a" );
        Value v1 = new Value( "test" );
        Assignment a1 = new Assignment( i1, v1 );

        Visitor visitor = new Visitor();
        //visitor.visit( a1 );
        a1.accept( visitor );
    }
}
