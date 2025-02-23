package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.layer;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.statement.std_lib.MemberFuncExecNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.FuncControlFlow;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.scripting.util.TypeUtils;
import com.jordanbunke.stipple_effect.layer.SELayer;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.LayerTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.LayerRep;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;

public abstract class LayerStatementNode extends MemberFuncExecNode {
    LayerStatementNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args, final TypeNode... expectedArgTypes
    ) {
        super(position, scope, LayerTypeNode.get(),
                args, TypeUtils.expectExact(expectedArgTypes));
    }

    protected final LayerRep layerRep(final SymbolTable symbolTable) {
        return (LayerRep) receiver.evaluate(symbolTable);
    }

    protected final SELayer evalLayer(final SymbolTable symbolTable) {
        final LayerRep l = layerRep(symbolTable);

        return l.project().getState().getLayers().get(l.index());
    }

    @Override
    public final FuncControlFlow execute(final SymbolTable symbolTable) {
        final LayerRep layer = layerRep(symbolTable);

        if (layer.index() >= layer.project().getState().getLayers().size())
            StatusUpdates.scriptActionNotPermitted(
                    "perform " + funcName(), "the layer object \"" +
                            receiver.receiver() + "\" no longer references a valid layer",
                    receiver.receiver().getPosition());
        else
            operation(layer, symbolTable);

        return FuncControlFlow.cont();
    }

    abstract void operation(final LayerRep layer, final SymbolTable symbolTable);
}
