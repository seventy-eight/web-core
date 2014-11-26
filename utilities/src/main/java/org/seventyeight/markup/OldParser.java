package org.seventyeight.markup;

public class OldParser extends Parser {

	public OldParser(Generator generator) {
		super(generator);

        markUps.add( new MarkUp( new char[]{'[', 'i', ']'}, MarkUpType.italic ) );
        markUps.add( new MarkUp( new char[]{'[', '/', 'i', ']'}, MarkUpType.italic ) );

        markUps.add( new MarkUp( new char[]{'[', 'b', ']'}, MarkUpType.bold ) );
        markUps.add( new MarkUp( new char[]{'[', '/', 'b', ']'}, MarkUpType.bold ) );

        markUps.add( new MarkUp( new char[]{'[', 'a', ']'}, MarkUpType.link ) );
        markUps.add( new MarkUp( new char[]{'[', '/', 'a', ']'}, MarkUpType.link ) );
        
        markUps.add( new MarkUp( new char[]{'[', 'u', ']'}, MarkUpType.underline ) );
        markUps.add( new MarkUp( new char[]{'[', '/', 'u', ']'}, MarkUpType.underline ) );

	}

	@Override
	public String getVersion() {
		return "1";
	}

}
