package org.seventyeight.parser.impl;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author cwolfgang
 */
public class SearchQueryParserTest {

    @Test
    public void test() {
        String s = "hej med dig";
        SearchQueryParser parser = new SearchQueryParser();
        List<String> tokens = parser.parse( s );
        System.out.println(tokens);
        assertThat(tokens.size(), is(3));
    }

    @Test
    public void test2() {
        String s = "type: snade";
        SearchQueryParser parser = new SearchQueryParser();
        List<String> tokens = parser.parse( s );
        System.out.println(tokens);
        assertThat(tokens.size(), is(3));
    }

    @Test
    public void test3() {
        String s = "type:: snade";
        SearchQueryParser parser = new SearchQueryParser();
        List<String> tokens = parser.parse( s );
        System.out.println(tokens);
        assertThat(tokens.size(), is(4));
    }

    @Test
    public void test4() {
        String s = "type: \"snade\"";
        SearchQueryParser parser = new SearchQueryParser();
        List<String> tokens = parser.parse( s );
        System.out.println(tokens);
        assertThat(tokens.size(), is(5));
    }
}
