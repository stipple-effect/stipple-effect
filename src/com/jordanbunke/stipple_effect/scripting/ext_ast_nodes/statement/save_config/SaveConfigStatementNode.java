package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.save_config;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.statement.std_lib.MemberFuncExecNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.scripting.util.TypeUtils;
import com.jordanbunke.stipple_effect.project.SaveConfig;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.SaveConfigTypeNode;

public abstract class SaveConfigStatementNode extends MemberFuncExecNode {
    public SaveConfigStatementNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final TypeNode... expectedArgTypes
    ) {
        super(position, scope, SaveConfigTypeNode.get(),
                args, TypeUtils.expectExact(expectedArgTypes));
    }

    protected final SaveConfig getSaveConfig(final SymbolTable symbolTable) {
        return (SaveConfig) receiver.evaluate(symbolTable);
    }
}
