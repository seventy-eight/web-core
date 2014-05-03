package org.seventyeight.parser;

import org.seventyeight.ast.StatementBlock;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class Parser {

    protected enum TokenType {
        SPLIT,
        SPLIT_AND_KEEP,
        CONTINUE
    }

    public List<String> parse(String string) {
        List<String> tokens = new LinkedList<String>();

        String current = "";
        for(int i = 0, l = string.length() ; i < l ; i++) {
            char c = string.charAt( i );
            switch( split( c ) ) {
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

                default:
                    current += c;
            }
        }

        if(current.length() > 0) {
            tokens.add( current );
        }

        return tokens;
    }

    protected abstract TokenType split(char c);

    public abstract StatementBlock getAST(List<String> tokens);
}
