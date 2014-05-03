package org.seventyeight.ast;

/**
 * @author cwolfgang
 */
public abstract class BinaryOperator extends Expression {
    protected Expression leftSide;
    protected Expression rightSide;

    public BinaryOperator(Expression leftSide, Expression rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    public Expression getLeftSide() {
        return leftSide;
    }

    public Expression getRightSide() {
        return rightSide;
    }
}
