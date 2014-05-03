package org.seventyeight.ast;

/**
 * @author cwolfgang
 */
public abstract class AbstractStatement implements Statement {
    protected Statement parent;

    @Override
    public Statement getParent() {
        return parent;
    }

    @Override
    public void setParent( Statement parent ) {
        this.parent = parent;
    }
}
