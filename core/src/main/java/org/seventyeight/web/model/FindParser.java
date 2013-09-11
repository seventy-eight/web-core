package org.seventyeight.web.model;

import org.seventyeight.web.Core;

import java.util.List;

/**
 * @author cwolfgang
 */
public class FindParser extends FeatureSearch {

    public void parse( List<String> tokens ) {
        for( String token : tokens ) {
            Searchable s = Core.getInstance().getSearchables().get( token );
            if( s == null ) {
                throw new IllegalStateException( "Invalid term " + token );
            }

            //s.search(  );
        }
    }
}
