package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.global;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.StippleEffect;

import java.awt.*;

public final class AssignColorNode extends GlobalStatementNode {
    public static final String PRIM_NAME = "set_primary",
            SEC_NAME = "set_secondary";

    private final boolean primary;

    private AssignColorNode(
            final TextPosition position, final ExpressionNode[] args,
            final boolean primary
    ) {
        super(position, args, TypeNode.getColor());

        this.primary = primary;
    }

    public static AssignColorNode primary(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new AssignColorNode(position, args, true);
    }

    public static AssignColorNode secondary(
            final TextPosition position, final ExpressionNode[] args
    ) {
        return new AssignColorNode(position, args, false);
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final Color c = (Color) arguments.evaluate(symbolTable)[0];

        StippleEffect.get().setColorIndexAndColor(primary
                ? StippleEffect.PRIMARY : StippleEffect.SECONDARY, c);

        return FuncControlFlow.cont();
    }

    @Override
    protected String funcName() {
        return primary ? PRIM_NAME : SEC_NAME;
    }
}
