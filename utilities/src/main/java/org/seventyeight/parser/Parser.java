package org.seventyeight.parser;

import org.seventyeight.ast.Root;

import java.util.Queue;

/**
 * @author cwolfgang
 */
public abstract class Parser {
    protected Tokenizer tokenizer = new Tokenizer();

    public Root parse(String string) {
        return getAST( tokenizer.tokenize( string ) );
    }

    public abstract Root getAST( Queue<String> tokens );
}
