package org.seventyeight.cache;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cwolfgang
 */
public class LruCache<KEY, VALUE> extends LinkedHashMap<KEY, VALUE> implements CacheStrategy<KEY, VALUE> {
    private final int maxSize;

    public LruCache( int maxSize ) {
        super(maxSize, 1.0f, true);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry( Map.Entry<KEY, VALUE> keyvalueEntry ) {
        return super.size() > maxSize;
    }
}
