package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.math;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.std_lib.DefFuncCallNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.Arguments;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.utility.Constants;

public final class PiNode extends DefFuncCallNode {
    public static final String PI = "PI";

    public PiNode(
            final TextPosition position
    ) {
        super(Arguments.none(), TypeNode.getFloat(), position);
    }

    @Override
    public Double evaluate(final SymbolTable symbolTable) {
        return Math.PI;
    }

    @Override
    protected String funcName() {
        return PI;
    }

    @Override
    public String toString() {
        return "$" + Constants.MATH_NAMESPACE + "." + funcName();
    }
}
