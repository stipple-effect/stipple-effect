package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.palette;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptSet;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.palette.Palette;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Predicate;

public final class PaletteColorSetGetterNode extends PaletteExpressionNode {
    public static final String COLORS = "colors", INCLUDED = "included";

    private final boolean included;

    private PaletteColorSetGetterNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final boolean included
    ) {
        super(position, scope, TypeNode.setOf(TypeNode.getColor()), args);

        this.included = included;
    }

    public static PaletteColorSetGetterNode included(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new PaletteColorSetGetterNode(position, scope, args, true);
    }

    public static PaletteColorSetGetterNode colors(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        return new PaletteColorSetGetterNode(position, scope, args, false);
    }

    @Override
    public ScriptSet evaluate(final SymbolTable symbolTable) {
        final Palette palette = getPalette(symbolTable);
        final Predicate<Color> filter = included
                ? palette::isIncluded : c -> true;

        return new ScriptSet(Arrays.stream(palette.getColors())
                .filter(filter).map(c -> c));
    }

    @Override
    protected String funcName() {
        return included ? INCLUDED : COLORS;
    }
}
