package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.LayerTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.LayerRep;

public final class UnlinkFramesNode extends SEExtStatementNode {
    public static final String NAME = "unlink_frames";

    public UnlinkFramesNode(
            final TextPosition position,
            final ExpressionNode[] args
    ) {
        super(position, args, LayerTypeNode.get());
    }

    @Override
    public FuncControlFlow execute(final SymbolTable symbolTable) {
        final LayerRep layer = (LayerRep) getArgs()[0].evaluate(symbolTable);

        layer.project().unlinkFramesInLayer(layer.index());

        return FuncControlFlow.cont();
    }

    @Override
    protected String callName() {
        return NAME;
    }
}
