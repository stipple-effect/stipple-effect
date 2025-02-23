package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.layer;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.std_lib.MemberFuncCallNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.scripting.util.TypeUtils;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.LayerTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.LayerRep;

public abstract class LayerExpressionNode extends MemberFuncCallNode {
    LayerExpressionNode(
            final TextPosition position, final ExpressionNode receiver,
            final TypeNode returnType,
            final ExpressionNode[] args, final TypeNode... expectedArgTypes
    ) {
        super(position, receiver, LayerTypeNode.get(),
                returnType, args, TypeUtils.expectExact(expectedArgTypes));
    }

    protected final LayerRep layerRep(final SymbolTable symbolTable) {
        return (LayerRep) receiver.evaluate(symbolTable);
    }
}
