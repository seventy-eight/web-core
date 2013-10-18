package org.seventyeight.markup;

/**
 * @author cwolfgang
 */
public class SimpleParser extends Parser {

    public SimpleParser( Generator generator ) {
        super( generator );

        markUps.add( new MarkUp( new char[]{'='}, MarkUpType.section ) );
        markUps.add( new MarkUp( new char[]{'=', '='}, MarkUpType.subSection ) );

        markUps.add( new MarkUp( new char[]{'\'', '\''}, MarkUpType.italic ) );
        markUps.add( new MarkUp( new char[]{'\'', '\'', '\''}, MarkUpType.bold ) );
        markUps.add( new MarkUp( new char[]{'\'', '\'', '\'', '\'', '\''}, MarkUpType.italicAndBold ) );

        markUps.add( new MarkUp( new char[]{'*'}, MarkUpType.unorderedList, true ) );

        //markUps.add( new MarkUp( new char[]{}, MarkUpType.newLine, true ) );
    }

    @Override
    public String getVersion() {
        return "1";
    }
}
