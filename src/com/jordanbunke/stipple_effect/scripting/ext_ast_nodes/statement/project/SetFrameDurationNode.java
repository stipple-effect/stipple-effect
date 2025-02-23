package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

public class SetFrameDurationNode extends ProjectStatementNode {
    public static final String NAME = "set_frame_duration";

    public SetFrameDurationNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args, TypeNode.getInt(), TypeNode.getFloat());
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final SEContext project = getProject(symbolTable);

        final Object[] vs = arguments.evaluate(symbolTable);
        final int frameIndex = (int) vs[0],
                fc = project.getState().getFrameCount();
        final double frameDuration = (double) vs[1];

        final String attempt = "set the relative duration of frame " +
                (frameIndex + 1);

        if (frameIndex < 0)
            StatusUpdates.scriptActionNotPermitted(attempt,
                    "the frame index (" + frameIndex + ") is negative",
                    arguments.get(0).getPosition());
        else if (frameIndex >= fc)
            StatusUpdates.scriptActionNotPermitted(attempt,
                    "the frame index (" + frameIndex + ") is " +
                            (frameIndex == fc ? "greater than " : "equal to ") +
                            "the frame count (" + fc + ")",
                    arguments.get(0).getPosition());
        else if (frameDuration < Constants.MIN_FRAME_DURATION ||
                frameDuration > Constants.MAX_FRAME_DURATION)
            StatusUpdates.scriptActionNotPermitted(attempt,
                    "the frame duration (" + frameDuration +
                            ") is out of bounds; " + Constants.MIN_FRAME_DURATION +
                            " <= frame_duration <= " + Constants.MAX_FRAME_DURATION,
                    arguments.get(1).getPosition());
        else
            project.changeFrameDuration(frameDuration, frameIndex);

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
