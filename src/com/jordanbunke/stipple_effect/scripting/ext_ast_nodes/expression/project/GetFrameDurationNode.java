package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;

public class GetFrameDurationNode extends ProjectExpressionNode {
    public static final String NAME = "get_frame_duration";

    public GetFrameDurationNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, TypeNode.getFloat(), args);
    }

    @Override
    public Double evaluate(final SymbolTable symbolTable) {
        final SEContext project = getProject(symbolTable);
        final int frameIndex = (int) arguments.evaluate(symbolTable)[0],
                fc = project.getState().getFrameCount();

        if (frameIndex < 0)
            ScriptErrorLog.fireError(ScriptErrorLog.Message.CUSTOM_RT,
                    arguments.get(0).getPosition(),
                    "The frame index (" + frameIndex + ") is negative");
        else if (frameIndex >= fc)
            ScriptErrorLog.fireError(ScriptErrorLog.Message.CUSTOM_RT,
                    arguments.get(0).getPosition(),
                    "The frame index (" + frameIndex + ") is " +
                            (frameIndex == fc ? "greater than " : "equal to ") +
                            "the frame count (" + fc + ")");
        else
            return project.getState().getFrameDurations().get(frameIndex);

        return null;
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
