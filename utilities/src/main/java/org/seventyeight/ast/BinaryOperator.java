package org.seventyeight.ast;

/**
 * @author cwolfgang
 */
public abstract class BinaryOperator extends Expression {
    protected Expression leftSide;
    protected Expression rightSide;
    protected Operator operator;

    public interface Operator {
        public String getValue();
    }

    public BinaryOperator(Operator operator, Expression leftSide, Expression rightSide) {
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

    public Operator getOperator() {
        return operator;
    }

    @Override
    public void accept( Visitor visitor ) {
        visitor.visit( this );
    }
}
