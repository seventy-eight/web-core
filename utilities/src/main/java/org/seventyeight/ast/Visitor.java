package org.seventyeight.ast;

/**
 * @author cwolfgang
 */
public class Visitor {
    public void visit() {
        
    }

    public void visit(Assignment assignment) {
        System.out.println("HEY!");
    }
}
