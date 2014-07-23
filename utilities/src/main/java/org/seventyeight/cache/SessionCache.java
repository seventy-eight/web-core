package org.seventyeight.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author cwolfgang
 */
public class SessionCache {

    private static Logger logger = LogManager.getLogger( SessionCache.class );

    private static Map<String, Record> cache;
    private DBStrategy dbStrategy;

    private boolean autoFlush = false;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private class Record {
        public Object record;
        private boolean dirty = true;

        public Record( Object record ) {
            this.record = record;
        }

        public Record( Object record, boolean dirty ) {
            this.record = record;
            this.dirty = dirty;
        }

        private boolean isDirty() {
            return dirty;
        }

        private void setDirty( boolean dirty ) {
            this.dirty = dirty;
        }

        @Override
        public String toString() {
            return record + " [dirty=" + dirty + "]";
        }
    }

    // Statistics
    private int misses = 0;
    private int hits = 0;
    private int writes = 0;

    public SessionCache( DBStrategy dbStrategy ) {
        this.dbStrategy = dbStrategy;
    }

    public SessionCache( DBStrategy dbStrategy, int maxSize ) {
        this.dbStrategy = dbStrategy;
        if(SessionCache.cache == null) {
            SessionCache.cache = Collections.synchronizedMap(new LruCache<String, Record>( maxSize ));
        }
    }

    public static void reset(int maxSize) {
        SessionCache.cache = Collections.synchronizedMap(new LruCache<String, Record>( maxSize ));
    }

    public void setAutoFlush( boolean autoFlush ) {
        this.autoFlush = autoFlush;
    }

    public void save(Object obj, String id) {
        logger.debug( "[CACHE] saving {}", id );
        Object record;
        if(autoFlush) {
            record = dbStrategy.save( obj, id );
        } else {
            record = dbStrategy.serialize( obj );
        }
        lock.writeLock().lock();
        try {
            cache.put( id, new Record(record, !autoFlush) );
            writes++;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public <TYPE> TYPE get(String id) {
        lock.readLock().lock();
        try {
            if(cache.containsKey( id )) {
                logger.debug( "[CACHE] retrieved {}", id );
                hits++;
                return (TYPE) dbStrategy.deserialize( cache.get( id ).record );
            } else {
                logger.debug( "[CACHE] missed {}", id );
                lock.readLock().unlock();
                TYPE o = resolve( id );
                misses++;
                lock.readLock().lock();
                return o;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    private <TYPE> TYPE resolve(String id) {
        logger.debug( "[CACHE] resolving {}", id );
        Object record = dbStrategy.get( id );
        if(record != null) {
            Object object = dbStrategy.deserialize( record );
            // Put the record in the cache
            lock.writeLock().lock();
            try {
                cache.put( id, new Record(record, false) );
            } finally {
                lock.writeLock().unlock();
            }

            return (TYPE) object;
        } else {
            logger.debug( "[CACHE] could not resolve {}", id );
            return null;
        }
    }

    public int getMisses() {
        return misses;
    }

    public int getHits() {
        return hits;
    }

    public int getWrites() {
        return writes;
    }

    public String mapToString() {
        return cache.toString();
    }

    public Map<String, Record> getCache() {
        return new HashMap<String, Record>( cache );
    }
}
