package org.seventyeight.cache;

/**
 * @author cwolfgang
 */
public interface CacheStrategy<KEY, VALUE> {
    public VALUE put(KEY key, VALUE value);
    public VALUE get(KEY key);
    public boolean containsKey(KEY key);
}
