package org.seventyeight.ast;

/**
 * @author cwolfgang
 */
public class Value extends Expression {

    protected String value;

    public Value(String value) {
        this.value = value;
    }

    @Override
    public void accept( Visitor visitor ) {
        visitor.visit( this );
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
