package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.save_config;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SaveConfig;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

public final class SetBoundNode extends SaveConfigStatementNode {
    public static final String LOWER = "set_lower", UPPER = "set_upper";

    private final boolean lower;

    private SetBoundNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final boolean lower
    ) {
        super(position, scope, args, TypeNode.getInt());

        this.lower = lower;
    }

    public static SetBoundNode lower(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new SetBoundNode(position, scope, args, true);
    }

    public static SetBoundNode upper(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        return new SetBoundNode(position, scope, args, false);
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final int bound = (int) arguments.evaluate(symbolTable)[0];
        final SaveConfig sc = getSaveConfig(symbolTable);

        if (bound < 0)
            StatusUpdates.scriptActionNotPermitted(
                    "set the " + (lower ? "lower" : "upper") +
                            " frame bound of the save configuration",
                    "the value supplied (" + bound + ") is negative",
                    arguments.get(0).getPosition());
        else {
            if (lower)
                sc.setLowerBound(bound);
            else
                sc.setUpperBound(bound);

            sc.setSaveRangeOfFrames(true);
        }

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return lower ? LOWER : UPPER;
    }
}
