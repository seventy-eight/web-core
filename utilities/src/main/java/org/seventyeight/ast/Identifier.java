package org.seventyeight.ast;

/**
 * @author cwolfgang
 */
public class Identifier extends Expression {
    protected String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public void accept( Visitor visitor ) {
        visitor.visit( this );
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
