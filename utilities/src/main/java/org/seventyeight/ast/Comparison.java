package org.seventyeight.ast;

/**
 * @author cwolfgang
 */
public class Comparison extends BinaryOperator {

    public enum ComparisonOperator implements Operator {
        EQUALS;

        @Override
        public String getValue() {
            return "=";
        }
    }

    public Comparison( Expression leftSide, Expression rightSide ) {
        super(ComparisonOperator.EQUALS, leftSide, rightSide );
    }

    @Override
    public void accept( Visitor visitor ) {
        visitor.visit( this );
    }
}
