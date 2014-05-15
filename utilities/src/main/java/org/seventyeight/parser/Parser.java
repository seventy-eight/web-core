package org.seventyeight.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.ast.Root;

import java.util.Queue;

/**
 * @author cwolfgang
 */
public abstract class Parser {
    private static Logger logger = LogManager.getLogger( Parser.class );
    protected Tokenizer tokenizer = new Tokenizer();

    public Root parse(String string) {
        logger.debug( "Parsing {}", string );
        return getAST( tokenizer.tokenize( string ) );
    }

    public abstract Root getAST( Queue<String> tokens );
}
