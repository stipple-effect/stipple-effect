package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.graphics;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.CollectionTypeNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.utility.Constants;

import java.util.Arrays;

import static com.jordanbunke.stipple_effect.scripting.util.LightingUtils.LightDirection;

public final class DirLightConstantNode extends GraphicsExpressionNode {
    private final LightDirection direction;

    public DirLightConstantNode(
            final TextPosition position, final LightDirection direction
    ) {
        super(position, TypeNode.arrayOf(TypeNode.getFloat()),
                new ExpressionNode[0]);

        this.direction = direction;
    }

    @Override
    public ScriptArray evaluate(final SymbolTable symbolTable) {
        return new ScriptArray(
                Arrays.stream(direction.vector).mapToObj(d -> d));
    }

    @Override
    public CollectionTypeNode getType(final SymbolTable symbolTable) {
        return TypeNode.arrayOf(TypeNode.getFloat());
    }

    @Override
    public String toString() {
        return "$" + Constants.GRAPHICS_NAMESPACE + "." + funcName();
    }

    @Override
    protected String funcName() {
        return direction.name();
    }
}
