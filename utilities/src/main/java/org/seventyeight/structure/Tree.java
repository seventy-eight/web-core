package org.seventyeight.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Tree<T> {

    private Node<T> root;

    public Tree( T rootData ) {
        root = new Node<T>( rootData );
    }

    public Node<T> getRoot() {
        return root;
    }

    public static class Node<T> {
        private T data;
        private Node<T> parent;
        private List<Node<T>> children;

        public Node( T data ) {
            this.data = data;
            this.children = new ArrayList<Node<T>>();
        }

        public void addNode( Node<T> node ) {
            node.parent = this;
            children.add( node );
        }

        public Node<T> addNode( T data ) {
            Node<T> node = new Node<T>( data );
            node.parent = this;
            children.add( node );

            return node;
        }

        public List<Node<T>> getChildren() {
            return children;
        }

        public T getData() {
            return data;
        }

        public boolean isLeaf() {
            return children.size() == 0;
        }

        /**
         * Add a string of data, returning the last node added, the leaf.
         */
        public Node<T> addString( List<T> dataString ) {
            Node<T> current = this;
            for( T d : dataString ) {
                current = current.addNode( d );
            }

            return current;
        }

        @Override
        public String toString() {
            return data + ", " + children.size() + " children";
        }
    }

    public void depthFirst( TraverseActions actions ) {
        depthFirst( root, actions );
    }

    public void depthFirst( Node<T> node, TraverseActions actions ) {
        if( node.isLeaf() ) {
            /* Do stuff */
            actions.leafAction( node );
        } else {
            actions.preNodeAction( node );
            for( Node<T> child : node.getChildren() ) {

                depthFirst( child, actions );

            }
            actions.postNodeAction( node );
        }
    }

    @Override
    public String toString() {
        return root + ". Children: " + root.getChildren();
    }

    public String visualize() {
        StringBuilder sb = new StringBuilder();
        Visualizer visualizer = new Visualizer();
        visualizer.sb = sb;

        depthFirst( visualizer );

        return sb.toString();
    }

    private static class Visualizer implements TraverseActions {

        private StringBuilder sb;
        private int counter = 0;
        private boolean newLine = false;

        @Override
        public void leafAction( Node node ) {
            if( newLine ) {
                sb.append( new String(new char[(counter*2)]).replace("\0", " ") );
            }
            sb.append( counter + "\n" );
            newLine = true;
        }

        @Override
        public void preNodeAction( Node node ) {
            if( newLine ) {
                sb.append( new String(new char[counter]).replace("\0", "-") );
                newLine = false;
            }
            sb.append( "a-" );
            counter++;
        }

        @Override
        public void postNodeAction( Node node ) {
            counter--;
        }
    }

    public static interface TraverseActions {
        public void leafAction( Node node );
        public void preNodeAction( Node node );
        public void postNodeAction( Node node );
    }
}
