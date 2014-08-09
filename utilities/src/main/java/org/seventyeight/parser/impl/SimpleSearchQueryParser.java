package org.seventyeight.parser.impl;

import org.seventyeight.ast.*;
import org.seventyeight.parser.Parser;

import java.util.Queue;

/**
 * @author cwolfgang
 */
public class SimpleSearchQueryParser extends Parser {

    @Override
    public Root getAST( Queue<String> tokens ) {
        //StatementBlock block = new StatementBlock();
        Root root = new Root();

        String last = null;
        boolean group = false;
        //for(int i = 0, l = tokens.size() ; i < l ; i++) {
        while(!tokens.isEmpty()) {
            String token = tokens.poll();
            if(token.equals( ':' ) && last != null) {
                String next = tokens.poll();
                if(next != null) {
                    Comparison comparison = new Comparison( new Identifier( last ), new Value( next ) );
                    root.getBlock().addStatement( comparison );
                    last = null;
                } else {
                    throw new IllegalStateException( "No value in assignment, " + last );
                }
            } else if(last != null) {
                root.getBlock().addStatement( new Value( last ) );
            }
        }

        return root;
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
