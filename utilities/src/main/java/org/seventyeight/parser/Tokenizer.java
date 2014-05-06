package org.seventyeight.parser;

import org.seventyeight.ast.Root;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author cwolfgang
 */
public class Tokenizer {

    protected enum TokenAction {
        SPLIT,
        SPLIT_AND_KEEP,
        QUOTED,
        CONTINUE
    }

    public LinkedList<String> tokenize(String string) {
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

    protected TokenAction identify( char c ) {
        switch( c ) {
            case ' ':
            case '\t':
            case '\n':
                return TokenAction.SPLIT;

            case '\'':
            case '"':
                return TokenAction.QUOTED;

            case ':':
            case '(':
            case ')':
            case '=':
                return TokenAction.SPLIT_AND_KEEP;

            default:
                return TokenAction.CONTINUE;
        }
    }
}
