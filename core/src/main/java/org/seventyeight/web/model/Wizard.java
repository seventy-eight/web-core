package org.seventyeight.web.model;

/**
 * @author cwolfgang
 */
public abstract class Wizard implements Node {
    @Override
    public String getMainTemplate() {
        return null;
    }

    public int getNextPage() {
        return 0;
    }
}
