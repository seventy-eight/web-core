package org.seventyeight.cache;

/**
 * @author cwolfgang
 */
public interface DBStrategy {
    /**
     * Given an ID return the corresponding record from the database. Return null if not found.
     */
    public Object get(String id);

    /**
     * Serialize an object
     */
    public void serialize(Object object);

    public Object deserialize(Object record);

    /**
     * Save an object and retrieve the record.
     * The returned record should be serialized by the db strategy.
     */
    public Object save(Object object, String id);
}
