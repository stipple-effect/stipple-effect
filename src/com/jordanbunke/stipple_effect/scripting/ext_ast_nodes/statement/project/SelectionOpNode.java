package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.utility.action.SEAction;

public final class SelectionOpNode extends CondProjectOpNode {
    public static final String
            INV = "invert_selection",
            ALL = "select_all",
            DESELECT = "deselect";

    private final SEAction operation;

    public SelectionOpNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final SEAction operation
    ) {
        super(position, scope, args);

        this.operation = operation;
    }

    @Override
    protected boolean condition(final SEContext project) {
        return true;
    }

    @Override
    protected String attempt() {
        return switch (operation) {
            case DESELECT -> "void the project's selection";
            case SELECT_ALL -> "select all pixels in the project canvas";
            case INVERT_SELECTION -> "invert the project selection";
            default -> "invalid";
        };
    }

    @Override
    protected String failReason(final SEContext project) {
        return ""; // never reached because condition() returns true
    }

    @Override
    protected void operation(final SEContext project) {
        operation.behaviour.accept(project);
    }

    @Override
    protected String funcName() {
        return operation.name().toLowerCase();
    }
}
