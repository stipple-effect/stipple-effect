package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.std_lib.DefFuncCallNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.util.Arguments;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.scripting.util.TypeUtils;
import com.jordanbunke.stipple_effect.utility.Constants;

public abstract class GlobalExpressionNode extends DefFuncCallNode {
    public GlobalExpressionNode(
            final TextPosition position, final TypeNode returnType,
            final ExpressionNode[] args, final TypeNode... expectedTypes
    ) {
        this(position, returnType, new Arguments(args,
                TypeUtils.expectExact(expectedTypes)));
    }

    public GlobalExpressionNode(
            final TextPosition position, final TypeNode returnType,
            final Arguments arguments
    ) {
        super(arguments, returnType, position);
    }

    @Override
    public String toString() {
        return "$" + Constants.SCRIPT_GLOBAL_NAMESPACE + "." + super.toString();
    }
}
