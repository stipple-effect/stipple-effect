package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.global;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.statement.std_lib.DefFuncExecNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.util.Arguments;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.scripting.util.TypeUtils;
import com.jordanbunke.stipple_effect.utility.Constants;

public abstract class GlobalStatementNode extends DefFuncExecNode {
    public GlobalStatementNode(
            final TextPosition position,
            final ExpressionNode[] args,
            final TypeNode... expectedTypes
    ) {
        super(new Arguments(args,
                TypeUtils.expectExact(expectedTypes)), position);
    }

    @Override
    public String toString() {
        return "$" + Constants.SCRIPT_GLOBAL_NAMESPACE + super.toString();
    }
}
