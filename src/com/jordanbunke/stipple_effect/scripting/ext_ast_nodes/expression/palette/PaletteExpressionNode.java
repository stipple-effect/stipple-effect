package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.palette;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.std_lib.MemberFuncCallNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.scripting.util.TypeUtils;
import com.jordanbunke.stipple_effect.palette.Palette;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.PaletteTypeNode;

public abstract class PaletteExpressionNode extends MemberFuncCallNode {
    public PaletteExpressionNode(
            final TextPosition position, final ExpressionNode scope,
            final TypeNode returnType,
            final ExpressionNode[] args, final TypeNode... expectedArgTypes
    ) {
        super(position, scope, PaletteTypeNode.get(),
                returnType, args, TypeUtils.expectExact(expectedArgTypes));
    }

    protected final Palette getPalette(final SymbolTable symbolTable) {
        return (Palette) receiver.evaluate(symbolTable);
    }
}
