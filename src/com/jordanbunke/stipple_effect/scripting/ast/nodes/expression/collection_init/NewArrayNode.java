package com.jordanbunke.stipple_effect.scripting.ast.nodes.expression.collection_init;

import com.jordanbunke.stipple_effect.scripting.ScrippleErrorListener;
import com.jordanbunke.stipple_effect.scripting.TextPosition;
import com.jordanbunke.stipple_effect.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.stipple_effect.scripting.ast.nodes.types.CollectionTypeNode;
import com.jordanbunke.stipple_effect.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.stipple_effect.scripting.ast.nodes.types.SimpleTypeNode;
import com.jordanbunke.stipple_effect.scripting.ast.symbol_table.SymbolTable;

public final class NewArrayNode extends ExpressionNode {
    private final TypeNode type;
    private final ExpressionNode length;

    public NewArrayNode(
            final TextPosition position,
            final TypeNode type,
            final ExpressionNode length
    ) {
        super(position);

        this.type = type;
        this.length = length;
    }

    @Override
    public void semanticErrorCheck(final SymbolTable symbolTable) {
        length.semanticErrorCheck(symbolTable);
        type.semanticErrorCheck(symbolTable);

        final TypeNode lengthType = length.getType(symbolTable);

        if (!lengthType.equals(new SimpleTypeNode(SimpleTypeNode.Type.INT)))
            ScrippleErrorListener.fireError(
                    ScrippleErrorListener.Message.ARR_LENGTH_NOT_INT,
                    getPosition(), lengthType.toString());
    }

    @Override
    public Object[] evaluate(final SymbolTable symbolTable) {
        final int l = (int) length.evaluate(symbolTable);

        if (l < 0)
            ScrippleErrorListener.fireError(
                    ScrippleErrorListener.Message.ARR_LENGTH_NEGATIVE,
                    getPosition(), String.valueOf(l));

        return type.createArray(l);
    }

    @Override
    public TypeNode getType(final SymbolTable symbolTable) {
        return new CollectionTypeNode(CollectionTypeNode.Type.ARRAY,
                new SimpleTypeNode(SimpleTypeNode.Type.RAW));
    }
}
