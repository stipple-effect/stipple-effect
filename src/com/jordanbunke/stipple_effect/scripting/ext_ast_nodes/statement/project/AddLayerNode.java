package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.utility.Constants;

public final class AddLayerNode extends CondProjectOpNode {
    public static final String ADD_NAME = "add_layer",
            DUPE_NAME = "duplicate_layer";

    private final boolean duplicate;

    private AddLayerNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final boolean duplicate
    ) {
        super(position, scope, args);

        this.duplicate = duplicate;
    }

    public static AddLayerNode newAdd(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new AddLayerNode(position, scope, args, false);
    }

    public static AddLayerNode newDupe(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new AddLayerNode(position, scope, args, true);
    }

    @Override
    protected boolean condition(final SEContext project) {
        return project.getState().canAddLayer();
    }

    @Override
    protected String attempt() {
        return duplicate
                ? "duplicate the current layer"
                : "add a layer to the project";
    }

    @Override
    protected String failReason(final SEContext project) {
        return "the project already contains " +
                Constants.MAX_NUM_LAYERS + " layers";
    }

    @Override
    protected void operation(final SEContext project) {
        if (duplicate)
            project.duplicateLayer();
        else
            project.addLayer();
    }

    @Override
    protected String funcName() {
        return duplicate ? DUPE_NAME : ADD_NAME;
    }
}
