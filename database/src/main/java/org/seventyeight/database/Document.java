package org.seventyeight.database;

/**
 * @author cwolfgang
 *         Date: 15-02-13
 *         Time: 20:33
 */
public interface Document {

    public static final String EXTENSIONS = "extensions";

    /**
     * Get a property from the {@link Document}
     * @param key
     * @param <T>
     * @return The property or null if non-existent
     */
    public <T> T get( String key );

    /**
     * Get a propertu from the {@link Document}. If the property is not found, return the default value.
     * @param key
     * @param defaultValue
     * @param <T>
     * @return
     */
    public <T> T get( String key, T defaultValue );

    /**
     * Set a property on the {@link Document}
     * @param key
     * @param value
     * @param <T>
     */
    public <T, R extends Document> R set( String key, T value );

}
