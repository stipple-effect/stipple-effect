package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;

public final class MergeLayersNode extends CondProjectOpNode {
    public static final String NAME = "merge_with_below";

    public MergeLayersNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args);
    }

    @Override
    protected boolean condition(final SEContext project) {
        return project.getState().canMoveLayerDown();
    }

    @Override
    protected String attempt() {
        return "merge the selected layer with the layer below it";
    }

    @Override
    protected String failReason(final SEContext project) {
        return "the selected layer is already the lowest in the project";
    }

    @Override
    protected void operation(final SEContext project) {
        project.mergeWithLayerBelow();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
