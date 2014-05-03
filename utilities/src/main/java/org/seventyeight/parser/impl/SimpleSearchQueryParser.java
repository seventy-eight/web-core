package org.seventyeight.parser.impl;

import org.seventyeight.ast.Assignment;
import org.seventyeight.ast.Identifier;
import org.seventyeight.ast.StatementBlock;
import org.seventyeight.ast.Value;
import org.seventyeight.parser.Parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author cwolfgang
 */
public class SimpleSearchQueryParser extends Parser {
    @Override
    protected Parser.TokenType identify( char c ) {
        switch( c ) {
            case ' ':
            case '\t':
            case '\n':
                return TokenType.SPLIT;

            case '\'':
            case '"':
                return TokenType.QUOTED;

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
    public StatementBlock getAST( Queue<String> tokens ) {
        StatementBlock block = new StatementBlock();

        String last = null;
        boolean group = false;
        //for(int i = 0, l = tokens.size() ; i < l ; i++) {
        while(!tokens.isEmpty()) {
            String token = tokens.poll();
            if(token.equals( ':' ) && last != null) {
                String next = tokens.poll();
                if(next != null) {
                    Assignment assignment = new Assignment( new Identifier( last ), new Value( next ) );
                    block.add( assignment );
                    last = null;
                } else {
                    throw new IllegalStateException( "No value in assignment, " + last );
                }
            } else if(last != null) {
                block.add( new Value( last ) );
            }
        }

        return block;
    }

    /*
    List<String> getGroup(Queue<String> tokens) {
        List<String> result = new LinkedList<String>(  );
        if(tokens.isEmpty()) {
            throw new IllegalArgumentException( "Empty token list" );
        }
        String token = tokens.poll();

        if(token.equals( '"' )) { // Group
            while( !tokens.isEmpty() && !(token = tokens.poll()).equals( '"' ) ) {
                result.add( token );
            }
        } else {
            result.add( token );
        }

        return result;
    }
    */
}
