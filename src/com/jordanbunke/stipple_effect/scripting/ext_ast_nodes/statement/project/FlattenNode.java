package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;

public final class FlattenNode extends CondProjectOpNode {
    public static final String NAME = "flatten";

    public FlattenNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args);
    }

    @Override
    protected boolean condition(final SEContext project) {
        return project.getState().canRemoveLayer();
    }

    @Override
    protected String attempt() {
        return "flatten the project";
    }

    @Override
    protected String failReason(final SEContext project) {
        return "the project only has a single layer";
    }

    @Override
    protected void operation(final SEContext project) {
        project.flatten();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
