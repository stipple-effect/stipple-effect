package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.save_config;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SaveConfig;

import static com.jordanbunke.stipple_effect.utility.DialogVals.SequenceOrder.*;

public final class SetDimNode extends SaveConfigStatementNode {
    public static final String NAME = "set_dim";

    public SetDimNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        super(position, scope, args, TypeNode.getBool());
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final SaveConfig sc = getSaveConfig(symbolTable);
        final boolean horizontal = (boolean) arguments.evaluate(symbolTable)[0];

        sc.setSequenceOrder(horizontal ? HORIZONTAL : VERTICAL);

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
