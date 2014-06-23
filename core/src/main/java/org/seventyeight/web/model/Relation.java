package org.seventyeight.web.model;

/**
 * @author cwolfgang
 */
public interface Relation {
    public enum BasicRelation implements Relation {
        CREATED_FOR;

        @Override
        public String getName() {
            return name();
        }
    }

    public String getName();
}
