package org.seventyeight.parser;

import org.junit.Test;
import org.seventyeight.parser.impl.SimpleSearchQueryParser;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author cwolfgang
 */
public class TokenizerTest {

    @Test
    public void test() {
        String s = "hej med dig";
        Tokenizer tokenizer = new Tokenizer();
        List<String> tokens = tokenizer.tokenize( s );
        System.out.println(tokens);
        assertThat( tokens.size(), is( 3 ) );
    }

    @Test
    public void test2() {
        String s = "type: snade";
        Tokenizer tokenizer = new Tokenizer();
        List<String> tokens = tokenizer.tokenize( s );
        System.out.println(tokens);
        assertThat(tokens.size(), is(3));
    }

    @Test
    public void test3() {
        String s = "type:: snade";
        Tokenizer tokenizer = new Tokenizer();
        List<String> tokens = tokenizer.tokenize( s );
        System.out.println(tokens);
        assertThat(tokens.size(), is(4));
    }

    @Test
    public void test4() {
        String s = "type: \"snade\"";
        Tokenizer tokenizer = new Tokenizer();
        List<String> tokens = tokenizer.tokenize( s );
        System.out.println(tokens);
        assertThat(tokens.size(), is(3));
    }

    @Test
    public void test5() {
        String s = "type: \"snade og ballade\"";
        Tokenizer tokenizer = new Tokenizer();
        List<String> tokens = tokenizer.tokenize( s );
        System.out.println(tokens);
        assertThat(tokens.size(), is(3));
    }
}
