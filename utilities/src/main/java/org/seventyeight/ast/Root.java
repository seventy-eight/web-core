package org.seventyeight.ast;

/**
 * @author cwolfgang
 */
public class Root {
    protected StatementBlock block = new StatementBlock();

    public StatementBlock getBlock() {
        return block;
    }

    @Override
    public String toString() {
        return "Root, size: " + block.size();
    }
}
