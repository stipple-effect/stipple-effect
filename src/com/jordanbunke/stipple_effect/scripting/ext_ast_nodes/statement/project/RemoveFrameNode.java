package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;

public final class RemoveFrameNode extends CondProjectOpNode {
    public static final String NAME = "remove_frame";

    public RemoveFrameNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args);
    }

    @Override
    protected boolean condition(final SEContext project) {
        return project.getState().canRemoveFrame();
    }

    @Override
    protected String attempt() {
        return "remove the current frame from the project";
    }

    @Override
    protected String failReason(final SEContext project) {
        return "the project only has a single frame";
    }

    @Override
    protected void operation(final SEContext project) {
        project.removeFrame();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
