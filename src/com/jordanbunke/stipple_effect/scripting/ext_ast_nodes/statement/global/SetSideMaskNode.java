package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.global;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.util.ScriptUtils;
import com.jordanbunke.stipple_effect.utility.DialogVals;

public final class SetSideMaskNode extends GlobalStatementNode {
    public static final String NAME = "set_side_mask";

    public SetSideMaskNode(
            final TextPosition position, final ExpressionNode[] args
    ) {
        super(position, args, TypeNode.arrayOf(TypeNode.getInt()));
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final int[] sideMask =
                ((ScriptArray) arguments.evaluate(symbolTable)[0])
                        .stream().mapToInt(s -> (int) s).toArray();

        if (ScriptUtils.invalidSideMask(sideMask, arguments.get(0)))
            return FuncControlFlow.cont();

        DialogVals.setOutlineSideMask(sideMask);

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
