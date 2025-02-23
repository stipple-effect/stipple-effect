package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.utility.DialogVals;

import java.util.Arrays;

public final class GetSideMaskNode extends GlobalExpressionNode {
    public static final String NAME = "get_side_mask";

    public GetSideMaskNode(
            final TextPosition position, final ExpressionNode[] args
    ) {
        super(position, TypeNode.arrayOf(TypeNode.getInt()), args);
    }

    @Override
    public ScriptArray evaluate(final SymbolTable symbolTable) {
        return new ScriptArray(Arrays.stream(
                DialogVals.getOutlineSideMask()).mapToObj(c -> c));
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
