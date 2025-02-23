package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.util.Arguments;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.utility.Constants;

public abstract class ConstantNode extends GlobalExpressionNode {
    public ConstantNode(final TextPosition position, final TypeNode returnType) {
        super(position, returnType, Arguments.none());
    }

    @Override
    public String toString() {
        return "$" + Constants.SCRIPT_GLOBAL_NAMESPACE + "." + funcName();
    }
}
