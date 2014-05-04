package org.seventyeight.parser;

import org.seventyeight.ast.Root;
import org.seventyeight.ast.StatementBlock;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author cwolfgang
 */
public abstract class Parser {

    protected enum TokenType {
        SPLIT,
        SPLIT_AND_KEEP,
        QUOTED,
        CONTINUE
    }

    public LinkedList<String> parse(String string) {
        LinkedList<String> tokens = new LinkedList<String>();

        String current = "";
        boolean quoted = false;
        char groupChar = '\0';
        for(int i = 0, l = string.length() ; i < l ; i++) {
            char c = string.charAt( i );
            if(quoted) {
                if( c != groupChar ) {
                    current += c;
                } else {
                    quoted = false;
                    tokens.add( current );
                    current = "";
                }
            } else {
                switch( identify( c ) ) {
                    case SPLIT:
                        if(current.length() > 0) {
                            tokens.add( current );
                            current = "";
                        }
                        break;

                    case SPLIT_AND_KEEP:
                        if(current.length() > 0) {
                            tokens.add( current );
                            current = "";
                        }
                        tokens.add( Character.toString( c ) );
                        break;

                    case QUOTED:
                        quoted = true;
                        groupChar = c;
                        break;
                    default:
                        current += c;
                }
            }
        }

        if(current.length() > 0) {
            tokens.add( current );
        }

        return tokens;
    }

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


    public abstract Root getAST( Queue<String> tokens );
}
