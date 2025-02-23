package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.global;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptSet;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.palette.Palette;

import java.awt.*;

public final class NewPaletteNode extends GlobalStatementNode {
    public static final String NAME = "new_pal";

    public NewPaletteNode(
            final TextPosition position, final ExpressionNode[] args
    ) {
        super(position, args, TypeNode.setOf(TypeNode.getColor()),
                TypeNode.getString());
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final Object[] vs = arguments.evaluate(symbolTable);
        final Color[] colors = convert((ScriptSet) vs[0]);
        final String name = (String) vs[1];

        StippleEffect.get().addPalette(new Palette(name, colors), true);

        return FuncControlFlow.cont();
    }

    private Color[] convert(final ScriptSet scriptSet) {
        return scriptSet.stream().map(c -> (Color) c).toArray(Color[]::new);
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
