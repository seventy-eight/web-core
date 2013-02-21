package org.seventyeight.web.utilities;

import org.seventyeight.web.model.Descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author cwolfgang
 *         Date: 12-01-13
 *         Time: 13:41
 */
public class DescriptorList extends ArrayList<Descriptor<?>> {

    public DescriptorList( Collection<? extends Descriptor<?>> c ) {
        super( c );
    }

    public void sortAlphabetically() {
        Collections.sort( this, new DisplayNameComparator() );
    }

    private class DisplayNameComparator implements Comparator<Descriptor> {

        @Override
        public int compare( Descriptor o1, Descriptor o2 ) {
            return o1.getDisplayName().compareTo( o2.getDisplayName() );
        }
    }
}
