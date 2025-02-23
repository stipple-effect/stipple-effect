package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.StippleEffect;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.PaletteTypeNode;

public final class PaletteGetterNode extends GlobalExpressionNode {
    public static final String GET = "get_pal", HAS = "has_pal";

    private final boolean get;

    private PaletteGetterNode(
            final TextPosition position, final ExpressionNode[] args,
            final boolean get
    ) {
        super(position, get ? PaletteTypeNode.get() : TypeNode.getBool(), args);

        this.get = get;
    }

    public static PaletteGetterNode newGet(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new PaletteGetterNode(position, args, true);
    }

    public static PaletteGetterNode newHas(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new PaletteGetterNode(position, args, false);
    }

    @Override
    public Object evaluate(final SymbolTable symbolTable) {
        final boolean hasPalette = StippleEffect.get().hasPaletteContents();

        if (!get)
            return hasPalette;

        if (hasPalette)
            return StippleEffect.get().getSelectedPalette();
        else {
            ScriptErrorLog.fireError(ScriptErrorLog.Message.CUSTOM_RT,
                    getPosition(), "There is no palette to get");

            return null;
        }
    }

    @Override
    protected String funcName() {
        return get ? GET : HAS;
    }
}
