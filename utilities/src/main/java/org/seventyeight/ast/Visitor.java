package org.seventyeight.ast;

/**
 * @author cwolfgang
 */
public class Visitor {

    public void visit(Root root) {
        root.getBlock().accept( this );
    }

    public void visit(StatementBlock statements) {
        for(Statement statement : statements) {
            statement.accept( this );
        }
    }

    public void visit(Statement statement) {
        System.out.println("HEY! Statement");
    }

    public void visit(Value value) {
        System.out.println("HEY value: " + value);
    }

    public void visit(Identifier identifier) {
        System.out.println( "Id." + identifier.getName() );
    }

    public void visit(Assignment assignment) {
        visit( (BinaryOperator)assignment );
    }

    public void visit(BinaryOperator operator) {
        operator.getLeftSide().accept( this );
        operator.getRightSide().accept( this );
    }
}
