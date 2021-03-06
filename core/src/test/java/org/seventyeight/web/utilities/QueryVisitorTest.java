package org.seventyeight.web.utilities;

import org.junit.Test;
import org.mockito.Mockito;
import org.seventyeight.ast.Comparison;
import org.seventyeight.ast.Identifier;
import org.seventyeight.ast.Value;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.web.CoreException;
import org.seventyeight.web.model.CoreSystem;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.Searchable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cwolfgang
 */
public class QueryVisitorTest {

    @Test
    public void test01() throws CoreException {
        //Core core = new DummyCore( new File( "" ), "" );
        CoreSystem system = Mockito.mock( CoreSystem.class );
        Map<String, Searchable> searchables = new HashMap<String, Searchable>(  );
        searchables.put( "test", new TestSearchable() );
        Mockito.doReturn( searchables ).when( system ).getSearchables();
        QueryVisitor visitor = new QueryVisitor( system );

        //Root root = new Root();
        Comparison a1 = new Comparison( new Identifier( "test" ), new Value( "snade" ) );
        //root.getBlock().addStatement( a1 );

        visitor.visit( a1 );

        System.out.println( visitor.getQuery() );
    }

    @Test
    public void testSingleKey() {
        CoreSystem system = Mockito.mock( CoreSystem.class );
        Map<String, String> searchables = new HashMap<String, String>(  );
        searchables.put( "test", "title" );
        Mockito.doReturn( searchables ).when( system ).getSearchKeyMap();

        QueryVisitor visitor = new QueryVisitor( system );

        Value v1 = new Value( "test" );

        visitor.visit( v1 );

        System.out.println( visitor.getQuery() );
    }

    @Test
    public void testMultipleKeys() {
        CoreSystem system = Mockito.mock( CoreSystem.class );
        Map<String, String> searchables = new HashMap<String, String>(  );
        searchables.put( "test", "title" );
        searchables.put( "test2", "tag" );
        searchables.put( "test3", "extensions.test.plugin1.test" );
        Mockito.doReturn( searchables ).when( system ).getSearchKeyMap();

        QueryVisitor visitor = new QueryVisitor( system );

        Value v1 = new Value( "test" );

        visitor.visit( v1 );

        System.out.println( visitor.getQuery() );
    }


    @Test
    public void testEmptySearchMap() {
        CoreSystem system = Mockito.mock( CoreSystem.class );
        Map<String, String> searchables = new HashMap<String, String>(  );
        Mockito.doReturn( searchables ).when( system ).getSearchKeyMap();

        QueryVisitor visitor = new QueryVisitor( system );

        Value v1 = new Value( "test" );

        visitor.visit( v1 );

        System.out.println( visitor.getQuery() );
    }

    class TestSearchable extends Searchable {

        @Override
        public Class<? extends Node> getClazz() {
            return null;  /* Implementation is a no op */
        }

        @Override
        public String getName() {
            return "Test searchable";
        }

        @Override
        public String getMethodName() {
            return "test";
        }

        @Override
        public MongoDBQuery search( String term ) {
            return new MongoDBQuery().is( "term", term ).exists( "hej" );
        }
    }
}
