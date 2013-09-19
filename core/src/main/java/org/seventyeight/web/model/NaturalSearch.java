package org.seventyeight.web.model;

import org.seventyeight.web.Core;

import java.util.StringTokenizer;

/**
 * @author cwolfgang
 */
public class NaturalSearch {

    private static final int TYPE_IDX = 0;

    private Class<? extends Resource<?>> type;

    public void parse( String query ) {
        String[] tokens = query.split( "\\s" );

        /* First find type */
        String t = tokens[TYPE_IDX];
        Core.getInstance().get
    }
}
