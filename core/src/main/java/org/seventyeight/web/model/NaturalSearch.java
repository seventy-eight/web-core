package org.seventyeight.web.model;

import org.seventyeight.structure.Tree;
import org.seventyeight.structure.Tuple;
import org.seventyeight.web.Core;

/**
 * @author cwolfgang
 */
public class NaturalSearch {

    private static final int TYPE_IDX = 0;

    private Class<? extends Resource<?>> type;

    private Tree<Tuple<String, NaturalSearchVerb>> tree;

    public void buildTree() {
        /* Top node does not contain any data */
        tree = new Tree<Tuple<String, NaturalSearchVerb>>( null );

        for( NaturalSearchable ns : Core.getInstance().getNaturalSearchables().values() ) {
            /* First level nodes are types */
            Tree.Node<Tuple<String, NaturalSearchVerb>> child = findChild( tree.getRoot(), ns.getType() );
            if( child == null ) {
                child = new Tree.Node<Tuple<String, NaturalSearchVerb>>( new Tuple<String, NaturalSearchVerb>( ns.getType(), null ) );
                tree.getRoot().addNode( child );
            }
            for( NaturalSearchVerb nsv : ns.getVerbs() ) {
                addVerbs( nsv, child );
            }
        }
    }

    private void addVerbs( NaturalSearchVerb verb, Tree.Node<Tuple<String, NaturalSearchVerb>> node ) {

        for( NaturalSearchVerb nsv : verb.getFollowingVerbs() ) {
            Tree.Node<Tuple<String, NaturalSearchVerb>> next = addVerb( nsv, node );
            addVerbs( nsv, next );
        }
    }

    private Tree.Node<Tuple<String, NaturalSearchVerb>> addVerb( NaturalSearchVerb verb, Tree.Node<Tuple<String, NaturalSearchVerb>> node ) {
        String[] tokens = verb.getVerb().split( "\\s" );
        Tree.Node<Tuple<String, NaturalSearchVerb>> current = null;
        for( String token : tokens ) {
            current = new Tree.Node<Tuple<String, NaturalSearchVerb>>( new Tuple<String, NaturalSearchVerb>( token, null ) );
        }

        current.getData().second = verb;
        return current;
    }

    private Tree.Node<Tuple<String, NaturalSearchVerb>> findChild( Tree.Node<Tuple<String, NaturalSearchVerb>> node, String string ) {
        for( Tree.Node<Tuple<String, NaturalSearchVerb>> child : node.getChildren() ) {
            if( child.getData().getFirst().equalsIgnoreCase( string ) ) {
                return child;
            }
        }

        return null;
    }

    public void parse( String query ) {
        String[] tokens = query.split( "\\s" );

        /* First find type */
        String type = tokens[TYPE_IDX];
        NaturalSearchable searchable = Core.getInstance().getNaturalSearchable( type );


    }

    private void traverse() {

    }
}
