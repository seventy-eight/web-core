package org.seventyeight.parser;

import org.seventyeight.ast.Root;

import java.util.Queue;

/**
 * @author cwolfgang
 */
public abstract class Parser {
    public abstract Root getAST( Queue<String> tokens );
}
