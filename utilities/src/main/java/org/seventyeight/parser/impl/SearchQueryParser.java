package org.seventyeight.parser.impl;

import org.seventyeight.ast.StatementBlock;
import org.seventyeight.parser.Parser;

import java.util.List;

/**
 * @author cwolfgang
 */
public class SearchQueryParser extends Parser {
    @Override
    protected Parser.TokenType split( char c ) {
        switch( c ) {
            case ' ':
            case '\t':
            case '\n':
                return TokenType.SPLIT;

            case '\'':
            case '"':
            case ':':
            case '(':
            case ')':
            case '=':
                return TokenType.SPLIT_AND_KEEP;

            default:
                return TokenType.CONTINUE;
        }
    }

    @Override
    public StatementBlock getAST( List<String> tokens ) {
        StatementBlock block = new StatementBlock();

        for(String token : tokens) {

        }

        return block;
    }
}
