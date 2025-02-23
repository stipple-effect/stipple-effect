package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.save_config;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SaveConfig;
import com.jordanbunke.stipple_effect.utility.Constants;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

public final class SaveConfigIntSetterNode extends SaveConfigStatementNode {
    public static final String SCALE_UP = "set_scale_up",
            FPD = "set_frames_per_dim", COUNT_FROM = "set_count_from",
            FPS = "set_fps";

    private final String property;

    private SaveConfigIntSetterNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final String property
    ) {
        super(position, scope, args, TypeNode.getInt());

        this.property = property;
    }

    public static SaveConfigIntSetterNode scaleUp(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new SaveConfigIntSetterNode(position, scope, args, SCALE_UP);
    }

    public static SaveConfigIntSetterNode fpd(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new SaveConfigIntSetterNode(position, scope, args, FPD);
    }

    public static SaveConfigIntSetterNode countFrom(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new SaveConfigIntSetterNode(position, scope, args, COUNT_FROM);
    }

    public static SaveConfigIntSetterNode fps(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new SaveConfigIntSetterNode(position, scope, args, FPS);
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final SaveConfig sc = getSaveConfig(symbolTable);
        final int toSet = (int) arguments.evaluate(symbolTable)[0];

        switch (property) {
            case COUNT_FROM -> sc.setCountFrom(toSet);
            case FPD -> {
                if (toSet <= 0)
                    StatusUpdates.scriptActionNotPermitted(
                            "set the frames per dimension of this save configuration",
                            "the value (" + toSet + ") is non-positive",
                            arguments.get(0).getPosition());
                else
                    sc.setFramesPerDim(toSet);
            }
            case SCALE_UP -> {
                if (toSet < Constants.MIN_SCALE_UP ||
                        toSet > Constants.MAX_SCALE_UP)
                    StatusUpdates.scriptActionNotPermitted(
                            "set the scale factor of this save configuration",
                            "the value (" + toSet + ") is out of bounds (" +
                                    Constants.MIN_SCALE_UP + "x - " +
                                    Constants.MAX_SCALE_UP + "x)",
                            arguments.get(0).getPosition());
                else
                    sc.setScaleUp(toSet);
            }
            case FPS -> sc.setFps(toSet);
        }

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return property;
    }
}
