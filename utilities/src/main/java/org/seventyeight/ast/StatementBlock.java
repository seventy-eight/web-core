package org.seventyeight.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class StatementBlock extends ArrayList<Statement> implements Statement {

    protected Statement parent;

    @Override
    public Statement getParent() {
        return parent;
    }

    @Override
    public void setParent( Statement parent ) {
        this.parent = parent;
    }

    @Override
    public void accept( Visitor visitor ) {
      /* Implementation is a no op */
    }
}
