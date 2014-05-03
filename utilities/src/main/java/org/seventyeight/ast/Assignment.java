package org.seventyeight.ast;

/**
 * @author cwolfgang
 */
public class Assignment extends BinaryOperator {
    public Assignment( Expression leftSide, Expression rightSide ) {
        super( leftSide, rightSide );
    }

    @Override
    public void accept( Visitor visitor ) {
        visitor.visit( this );
    }
}
