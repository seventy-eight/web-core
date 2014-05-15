package org.seventyeight.web.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.ast.Comparison;
import org.seventyeight.ast.Identifier;
import org.seventyeight.ast.Root;
import org.seventyeight.ast.Value;
import org.seventyeight.parser.Parser;

import java.util.Queue;

/**
 * @author cwolfgang
 */
public class QueryParser extends Parser {
    private static Logger logger = LogManager.getLogger( QueryParser.class );
    @Override
    public Root getAST( Queue<String> tokens ) {
        String previous = null;
        Root root = new Root();

        logger.debug( "TOKENs: {}", tokens );

        while(!tokens.isEmpty()) {
            String token = tokens.poll();
            logger.debug( "TOKEN: {}", token );

            if(previous != null) {
                if(isComparison( token )) {
                    String next = tokens.poll();
                    if(next == null) {
                        throw new IllegalStateException( "Not a valid query" );
                    }

                    logger.debug( "Adding assignment" );
                    root.getBlock().addStatement( new Comparison( new Identifier( previous ), new Value( next ) ) );
                } else {
                    logger.debug( "Adding block" );
                    root.getBlock().addStatement( new Value( previous ) );
                }
            }

            previous = token;
        }

        if(previous != null) {
            logger.debug( "Adding block" );
            root.getBlock().addStatement( new Value( previous ) );
        }

        return root;
    }

    protected boolean isComparison(String s) {
        if(s.length() == 1) {
            if(s.charAt( 0 ) == ':' || s.charAt( 0 ) == '=') {
                return true;
            }
        } else if(s.length() == 2) {
            if(s.equals( "==" )) {
                return true;
            }
        }

        return false;
    }
}
