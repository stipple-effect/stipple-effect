package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.graphics;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.std_lib.DefFuncCallNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.util.Arguments;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.scripting.util.TypeUtils;
import com.jordanbunke.stipple_effect.utility.Constants;

public abstract class GraphicsExpressionNode extends DefFuncCallNode {
    public GraphicsExpressionNode(
            final TextPosition position, final TypeNode returnType,
            final ExpressionNode[] args, final TypeNode... expectedTypes
    ) {
        super(new Arguments(args, TypeUtils.expectExact(expectedTypes)),
                returnType, position);
    }

    @Override
    public String toString() {
        return "$" + Constants.GRAPHICS_NAMESPACE + "." + super.toString();
    }
}
