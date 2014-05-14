package org.seventyeight.web.utilities;

import org.junit.Test;
import org.mockito.Mockito;
import org.seventyeight.ast.Root;
import org.seventyeight.web.model.CoreSystem;
import org.seventyeight.web.model.Searchable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cwolfgang
 */
public class QueryParserTest {

    @Test
    public void test01() {
        QueryParser parser = new QueryParser();
        Root root = parser.parse( "test" );

        System.out.println("Root: " + root);
    }

    @Test
    public void test02() {
        CoreSystem system = Mockito.mock( CoreSystem.class );
        Map<String, String> searchables = new HashMap<String, String>(  );
        searchables.put( "test", "title" );
        Mockito.doReturn( searchables ).when( system ).getSearchKeyMap();

        QueryParser parser = new QueryParser();
        Root root = parser.parse( "test" );

        QueryVisitor visitor = new QueryVisitor( system );
        visitor.visit( root );

        System.out.println("Query: " + visitor.getQuery());
    }

    @Test
    public void test03() {
        CoreSystem system = Mockito.mock( CoreSystem.class );
        Map<String, String> searchables = new HashMap<String, String>(  );
        searchables.put( "test", "title" );
        Mockito.doReturn( searchables ).when( system ).getSearchKeyMap();

        QueryParser parser = new QueryParser();
        Root root = parser.parse( "type:user wolle" );

        QueryVisitor visitor = new QueryVisitor( system );
        visitor.visit( root );

        System.out.println("Query: " + visitor.getQuery());
    }
}
