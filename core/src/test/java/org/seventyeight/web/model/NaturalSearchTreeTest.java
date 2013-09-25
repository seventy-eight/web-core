package org.seventyeight.web.model;

import org.junit.Test;
import org.seventyeight.web.nodes.Group;
import org.seventyeight.web.nodes.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class NaturalSearchTreeTest {

    @Test
    public void test() {
        List<NaturalSearchable> searchables = new ArrayList<NaturalSearchable>(  );

        TestData td1 = new TestData();
        td1.getSearchTreeString().add( new UserData( "users" ) );

        searchables.add( td1 );

        TestData td2 = new TestData();
        td2.getSearchTreeString().add( new Tagged( "tagged" ) );

        searchables.add( td2 );

        TestData td3 = new TestData();
        td3.getSearchTreeString().add( new GroupData( "snade" ) );

        searchables.add( td3 );

        NaturalSearchTree tree = NaturalSearchTree.build( searchables );

        System.out.println( tree.visualize() );
    }

    public static class UserData extends NaturalSearchTree.AbstractSearchTreeData {

        public UserData( String term ) {
            super( term );
        }

        @Override
        public Class<? extends Resource<?>> produces() {
            return User.class;
        }
    }

    public static class GroupData extends NaturalSearchTree.AbstractSearchTreeData {

        public GroupData( String term ) {
            super( term );
        }

        @Override
        public Class<? extends Resource<?>> produces() {
            return Group.class;
        }
    }

    public static class Tagged extends NaturalSearchTree.AbstractSearchTreeData {

        public Tagged( String term ) {
            super( term );
        }

        @Override
        public Class<? extends Resource<?>> consumes() {
            return User.class;
        }
    }

    public static class TestData implements NaturalSearchable {

        public List<NaturalSearchTree.AbstractSearchTreeData> list = new ArrayList<NaturalSearchTree.AbstractSearchTreeData>(  );

        @Override
        public String getType() {
            return "test";
        }

        @Override
        public List<NaturalSearchVerb> getVerbs() {
            return null;
        }

        @Override
        public List<NaturalSearchTree.AbstractSearchTreeData> getSearchTreeString() {
            return list;
        }
    }
}
