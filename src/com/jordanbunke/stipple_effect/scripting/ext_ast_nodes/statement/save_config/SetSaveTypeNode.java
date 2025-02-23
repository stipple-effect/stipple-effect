package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.save_config;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SaveConfig;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

public final class SetSaveTypeNode extends SaveConfigStatementNode {
    public static final String NAME = "set_save_type";

    public SetSaveTypeNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        super(position, scope, args, TypeNode.getInt());
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final int saveTypeIndex = (int) arguments.evaluate(symbolTable)[0];
        final SaveConfig sc = getSaveConfig(symbolTable);

        if (saveTypeIndex < 0 || saveTypeIndex >= SaveConfig.SaveType.values().length)
            StatusUpdates.scriptActionNotPermitted(
                    "set the save type of the save configuration",
                    "the value (" + saveTypeIndex +
                            ") is not a valid save type index",
                    arguments.get(0).getPosition());
        else
            sc.setSaveType(SaveConfig.SaveType.values()[saveTypeIndex]);

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
