package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.graphics;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.LightTypeNode;

public abstract class LightConstructorNode extends GraphicsExpressionNode {
    LightConstructorNode(
            final TextPosition position, final ExpressionNode[] args,
            final TypeNode... expectedTypes
    ) {
        super(position, LightTypeNode.get(), args, expectedTypes);
    }
}
