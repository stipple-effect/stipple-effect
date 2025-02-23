package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.StippleEffect;

import java.awt.*;

public final class GetColorNode extends GlobalExpressionNode {
    public static final String PRIM_NAME = "get_primary",
            SEC_NAME = "get_secondary";

    private final boolean primary;

    private GetColorNode(
            final TextPosition position, final ExpressionNode[] args,
            final boolean primary
    ) {
        super(position, TypeNode.getColor(), args);

        this.primary = primary;
    }

    public static GetColorNode primary(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new GetColorNode(position, args, true);
    }

    public static GetColorNode secondary(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new GetColorNode(position, args, false);
    }

    @Override
    public Color evaluate(final SymbolTable symbolTable) {
        return primary
                ? StippleEffect.get().getPrimary()
                : StippleEffect.get().getSecondary();
    }

    @Override
    protected String funcName() {
        return primary ? PRIM_NAME : SEC_NAME;
    }
}
