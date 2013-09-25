package org.seventyeight.structure;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author cwolfgang
 */
public class TreeTest {

    @Test
    public void basicTest() {
        Tree<String> tree = new Tree<String>( "top level" );

        assertThat( tree.getRoot().getData(), is( "top level" ) );
    }

    @Test
    public void twoLevelTest() {
        Tree<String> tree = new Tree<String>( "top level" );

        assertThat( tree.getRoot().getData(), is( "top level" ) );

        Tree.Node<String> c1 = new Tree.Node<String>( "two" );
        tree.getRoot().addNode( c1 );

        assertThat( tree.getRoot().getChildren().size(), is( 1 ) );
        System.out.println(tree.getRoot().getChildren());
        assertThat( tree.getRoot().getChildren().get( 0 ).getData(), is( "two" ) );
    }
}
