package org.seventyeight.web.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.structure.Tree;

import java.util.List;

/**
 * <pre>
 *
 *
 * node - type
 *
 *
 * </pre>
 *
 *
 * @author cwolfgang
 */
public class NaturalSearchTree extends Tree<NaturalSearchTree.AbstractSearchTreeData> {

    private static Logger logger = LogManager.getLogger( NaturalSearchTree.class );

    public NaturalSearchTree( AbstractSearchTreeData rootData ) {
        super( rootData );
    }

    public static abstract class AbstractSearchTreeData {

        protected String term;

        public AbstractSearchTreeData( String term ) {
            this.term = term;
        }

        public String getTerm() {
            return term;
        }

        public Class<? extends Resource<?>> consumes() {
            return null;
        }
        public Class<? extends Resource<?>> produces() {
            return null;
        }
    }

    public static NaturalSearchTree build( List<NaturalSearchable> searchables ) {

        NaturalSearchTree tree = initializeTree( searchables );
        iterate( tree, searchables );

        return tree;
    }

    private static void iterate( NaturalSearchTree tree, List<NaturalSearchable> searchables ) {
        logger.debug( "ITERATING.... " + searchables.size() );

        for( NaturalSearchable s : searchables ) {
            logger.debug( "STRING: " + s.getSearchTreeString() );
            if( s.getSearchTreeString() != null && s.getSearchTreeString().size() > 0 && s.getSearchTreeString().get( 0 ).consumes() != null ) {
                logger.debug( "Iterate adding adder" );
                Adder adder = new Adder();
                adder.searchable = s;
                tree.depthFirst( adder );
            }
        }
    }

    private static class Adder implements TraverseActions {

        private NaturalSearchable searchable;

        @Override
        public void leafAction( Node node ) {
            AbstractSearchTreeData data = (AbstractSearchTreeData) node.getData();

            System.out.println("YAY");
            AbstractSearchTreeData next = searchable.getSearchTreeString().get( 0 );
            if( next.consumes() != null && data.produces() != null ) {
                if( data.produces().equals( next.consumes() ) ) {
                    logger.debug( "Adding nodes" );
                    node.addString( searchable.getSearchTreeString() );
                    logger.debug( "Nodes added" );
                }
            }
        }

        @Override
        public void preNodeAction( Node node ) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void postNodeAction( Node node ) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private static NaturalSearchTree initializeTree( List<NaturalSearchable> searchables ) {
        logger.debug( "Initializing tree" );
        NaturalSearchTree tree = new NaturalSearchTree( null );

        /* Find those strings consuming everything, eg == null */
        for( NaturalSearchable s : searchables ) {
            if( s.getSearchTreeString() != null && s.getSearchTreeString().size() > 0 && s.getSearchTreeString().get( 0 ).consumes() == null ) {
                Node current = tree.getRoot();
                for( AbstractSearchTreeData data : s.getSearchTreeString() ) {
                    Node child = new Node( data );
                    current.addNode( child );

                    current = child;
                }
            }
        }

        return tree;
    }
}
