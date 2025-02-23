package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.project;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.LayerTypeNode;
import com.jordanbunke.stipple_effect.scripting.util.LayerRep;

import java.util.stream.IntStream;

public final class GetLayersNode extends ProjectExpressionNode {
    public static final String NAME = "get_layers";

    public GetLayersNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, TypeNode.arrayOf(LayerTypeNode.get()), args);
    }

    @Override
    public ScriptArray evaluate(final SymbolTable symbolTable) {
        final SEContext project = getProject(symbolTable);

        return new ScriptArray(
                IntStream.range(0, project.getState().getLayers().size())
                        .mapToObj(i -> new LayerRep(project, i)));
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
