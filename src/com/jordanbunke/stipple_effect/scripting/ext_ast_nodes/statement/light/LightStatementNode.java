package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.light;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.statement.std_lib.MemberFuncExecNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.scripting.util.TypeUtils;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.LightTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.Light;

public abstract class LightStatementNode extends MemberFuncExecNode {
    LightStatementNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final TypeNode... expectedArgTypes
    ) {
        super(position, scope, LightTypeNode.get(),
                args, TypeUtils.expectExact(expectedArgTypes));
    }

    final Light light(final SymbolTable symbolTable) {
        return (Light) receiver.evaluate(symbolTable);
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        operation(light(symbolTable), symbolTable);

        return FuncControlFlow.cont();
    }

    abstract void operation(final Light light, final SymbolTable symbolTable);
}
