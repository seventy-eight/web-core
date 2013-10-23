package org.seventyeight.markup;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class Parser {

    private static Logger logger = Logger.getLogger( Parser.class );

    private Generator generator;

    public static class MarkUp {
        char[] sequence;
        MarkUpType type;

        int count = 0;
        boolean consumeAll = false;
        int consumed = 0;

        public MarkUp( char[] sequence, MarkUpType type ) {
            this.sequence = sequence;
            this.type = type;
        }

        public MarkUp( char[] sequence, MarkUpType type, boolean consumeAll ) {
            this.sequence = sequence;
            this.type = type;
            this.consumeAll = consumeAll;
        }

        @Override
        public String toString() {
            return type.name();
        }
    }

    public Parser( Generator generator ) {
        this.generator = generator;
    }

    private static final MarkUp emptyMarkUp = new MarkUp( new char[]{}, MarkUpType.missing );

    protected List<MarkUp> markUps = new ArrayList<MarkUp>();

    public StringBuilder parse( String text ) {
        StringBuilder output = new StringBuilder();
        parse( text, output );
        return output;
    }

    /**
     * Parse the text string and provide the result in the {@link StringBuilder}.
     */
    public void parse( String text, StringBuilder output ) {
        boolean beginningOfLine = true;
        for( int i = 0 ; i < text.length() ; i++ ) {
            int move = getMarkUp( text, output, beginningOfLine, i );
            if( move > 0 ) {
                i += move - 1;
            } else {
                output.append( text.charAt( i ) );
            }

            if( text.charAt( i ) == '\n' ) {
                // If there's a new line and there are no characters in the current line, it's empty
                if( beginningOfLine ) {
                    generator.onEmptyLine( output );
                }
                beginningOfLine = true;
            } else {
                beginningOfLine &= Character.isWhitespace( text.charAt( i ) );
            }
        }

        // The last line is also considered an empty line
        generator.onEmptyLine( output );
    }

    /**
     * Use the {@link MarkUp} that suits the char sequence the most. Return the number of characters consumed.
     * @param text - The text string to be parsed.
     * @param output - The {@link StringBuilder} to append the translated text.
     * @param beginningOfLine - True if there are only whitespaces from the beginning of the line to the current character
     * @param idx - The index of the current character
     */
    private int getMarkUp( String text, StringBuilder output, boolean beginningOfLine, int idx ) {
        MarkUp selected = emptyMarkUp;
        int consumed = 0;

        // Select the best matched sequence
        for( MarkUp m : markUps ) {
            if( m.sequence[0] == text.charAt( idx ) && !m.consumeAll ) {
                if( parseSequence( text, m, idx ) ) {
                    if( m.sequence.length > selected.sequence.length ) {
                        selected = m;
                        consumed = m.sequence.length;
                    }
                }
            }

            if( m.sequence[0] == text.charAt( idx ) && m.consumeAll && beginningOfLine ) {
                consumed = consumeAll( text, m, idx );
                selected = m;
                break;
            }
        }

        if( selected.sequence.length > 0 ) {
            selectType( output, selected, consumed );
            return consumed;
        } else {
            return 0;
        }
    }

    /**
     *
     * @param text
     * @param markUp
     * @param idx
     * @return
     */
    private int consumeAll( String text, MarkUp markUp, int idx ) {
        int count = 0;
        while( text.charAt( idx + count ) == markUp.sequence[0] ) {
            count++;
        }

        logger.debug( "Consumed " + count );

        return count;
    }

    private void selectType( StringBuilder output, MarkUp markUp, int consumed ) {
        logger.debug( markUp + ", " + consumed + ", " + markUp.consumed );
        switch( markUp.type ) {
            case section:
                generator.section( output, markUp.count );
                break;

            case subSection:
                generator.subSection( output, markUp.count );
                break;

            case italic:
                generator.italic( output, markUp.count );
                break;

            case bold:
                generator.bold( output, markUp.count );
                break;

            case italicAndBold:
                generator.italicAndBold( output, markUp.count );
                break;

            case unorderedList:
                generator.unorderedList( output, markUp, consumed );
                break;

            case orderedList:
                generator.orderedList( output, markUp, consumed );
                break;
        }

        markUp.count++;
    }

    private boolean parseSequence( String text, MarkUp markUp, int idx ) {
        for( int i = 1 ; i < markUp.sequence.length ; i++ ) {
            if( markUp.sequence[i] != text.charAt( idx + i ) ) {
                return false;
            }
        }
        return true;
    }

    public abstract String getVersion();

    public String getGeneratorVersion() {
        return generator.getVersion();
    }
}
