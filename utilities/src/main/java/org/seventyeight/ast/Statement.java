package org.seventyeight.ast;

/**
 * @author cwolfgang
 */
public interface Statement {
    public Statement getParent();
    public void setParent(Statement parent);
    public void accept(Visitor visitor);
}
