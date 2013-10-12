package org.seventyeight.markup;

/**
 * @author cwolfgang
 */
public interface Generator {
    public void section( StringBuilder output, int count );
    public void subSection( StringBuilder output, int count );

    public void italic( StringBuilder output, int count );
    public void bold( StringBuilder output, int count );
    public void italicAndBold( StringBuilder output, int count );

    public void unorderedList( StringBuilder output, Parser.MarkUp markUp, int consumed );
    public void orderedList( StringBuilder output, Parser.MarkUp markUp, int consumed );

    public void onEmptyLine( StringBuilder output );

    public Parser.MarkUp getLastMarkUp();
}
