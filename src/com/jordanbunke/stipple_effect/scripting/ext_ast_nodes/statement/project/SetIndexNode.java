package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

public final class SetIndexNode extends ProjectStatementNode {
    public static final String LAYER_NAME = "set_layer_index",
            FRAME_NAME = "set_frame_index";

    private final boolean frame;

    private SetIndexNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final boolean frame
    ) {
        super(position, scope, args, TypeNode.getInt());

        this.frame = frame;
    }

    public static SetIndexNode newFrame(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new SetIndexNode(position, scope, args, true);
    }

    public static SetIndexNode newLayer(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new SetIndexNode(position, scope, args, false);
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);
        final SEContext project = getProject(symbolTable);
        final int index = (int) vs[0], size = frame
                ? project.getState().getFrameCount()
                : project.getState().getLayers().size();
        final String unit = frame ? "frame" : "layer";

        if (index < 0)
            StatusUpdates.scriptActionNotPermitted(
                    "set this project's " + unit + "index",
                    "the supplied index is negative",
                    arguments.get(0).getPosition());
        else if (index >= size)
            StatusUpdates.scriptActionNotPermitted(
                    "set this project's " + unit + "index",
                    "the supplied index (" + index +
                            ") is out of bounds for this project's " + size +
                            " " + unit + "s",
                    arguments.get(0).getPosition());
        else if (frame)
            project.getState().setFrameIndex(index);
        else
            project.getState().setLayerEditIndex(index);

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return frame ? FRAME_NAME : LAYER_NAME;
    }
}
