package org.seventyeight.markup;

import org.apache.log4j.Logger;

/**
 * @author cwolfgang
 */
public class HtmlGenerator implements Generator {

    private static Logger logger = Logger.getLogger( HtmlGenerator.class );

    private Parser.MarkUp lastMarkUp;

    @Override
    public void section( StringBuilder output, int count ) {
        if( count % 2 == 0 ) {
            output.append( "<h1>" );
        } else {
            output.append( "</h1>" );
        }
    }

    @Override
    public void subSection( StringBuilder output, int count ) {
        if( count % 2 == 0 ) {
            output.append( "<h2>" );
        } else {
            output.append( "</h2>" );
        }
    }

    @Override
    public void italic( StringBuilder output, int count ) {
        if( count % 2 == 0 ) {
            output.append( "<span style=\"font-style: italic\">" );
        } else {
            output.append( "</span>" );
        }
    }

    @Override
    public void bold( StringBuilder output, int count ) {
        if( count % 2 == 0 ) {
            output.append( "<span style=\"font-weight: bold\">" );
        } else {
            output.append( "</span>" );
        }
    }

    @Override
    public void italicAndBold( StringBuilder output, int count ) {
        if( count % 2 == 0 ) {
            output.append( "<span style=\"font-style: italic; font-weight: bold\">" );
        } else {
            output.append( "</span>" );
        }
    }

    @Override
    public void unorderedList( StringBuilder output, Parser.MarkUp markUp, int consumed ) {
        list( output, markUp, consumed, "<ul>", "</ul>" );
    }

    @Override
    public void orderedList( StringBuilder output, Parser.MarkUp markUp, int consumed ) {
        list( output, markUp, consumed, "<ol>", "</ol>" );
    }

    private void list( StringBuilder output, Parser.MarkUp markUp, int consumed, String startTag, String endTag ) {
        int diff = consumed - markUp.consumed;

        // Be sure to close last list item
        if( markUp.consumed > 0 ) {
            output.append( "</li>" );
        }

        logger.debug( "DIFF: " + diff );

        if( diff > 0 ) {
            for( int i = 0 ; i < diff ; i++ ) {
                output.append( startTag );
            }
        } else if( diff < 0 ) {
            for( int i = 0 ; i > diff ; i-- ) {
                output.append( endTag );
            }
        }

        output.append( "<li>" );

        lastMarkUp = markUp;

        markUp.consumed = consumed;
    }

    @Override
    public void onEmptyLine( StringBuilder output ) {
        logger.debug( "On empty line" );
        Parser.MarkUp m = getLastMarkUp();

        if( m != null ) {
            // It's time to some unfinished business
            switch( m.type ) {
                case unorderedList:
                    endList( output, m , "</ul>" );
                    break;

                case orderedList:
                    endList( output, m , "</ol>" );
                    break;
            }
        }

        // In either case
        output.append( "<br />" );
    }

    private void endList( StringBuilder output, Parser.MarkUp m, String tagEnd ) {

        // Be sure to close last list item
        output.append( "</li>" );

        int diff = m.consumed;
        for( int i = 0 ; i < diff ; i++ ) {
            output.append( tagEnd );
        }
    }

    @Override
    public Parser.MarkUp getLastMarkUp() {
        return lastMarkUp;
    }
}
