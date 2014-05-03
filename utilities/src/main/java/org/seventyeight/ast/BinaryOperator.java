package org.seventyeight.ast;

/**
 * @author cwolfgang
 */
public abstract class BinaryOperator extends Expression {
    protected Expression leftSide;
    protected Expression rightSide;
    protected String operator;

    public BinaryOperator(String operator, Expression leftSide, Expression rightSide) {
        this.operator = operator;
        this.leftSide = leftSide;
        this.rightSide = rightSide;

        leftSide.setParent( this );
        rightSide.setParent( this );
    }

    public Expression getLeftSide() {
        return leftSide;
    }

    public Expression getRightSide() {
        return rightSide;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public void accept( Visitor visitor ) {
        visitor.visit( this );
    }
}
