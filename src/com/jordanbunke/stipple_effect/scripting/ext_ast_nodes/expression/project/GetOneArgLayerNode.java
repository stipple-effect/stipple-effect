package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.ScriptErrorLog;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.layer.SELayer;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.scripting.util.LayerRep;

import java.util.List;

public final class GetOneArgLayerNode extends GetLayerNode {
    public GetOneArgLayerNode(
            final TextPosition position,
            final ExpressionNode scope, final ExpressionNode[] args
    ) {
        super(position, scope, args, TypeNode.wildcard());
    }

    @Override
    public LayerRep evaluate(final SymbolTable symbolTable) {
        final SEContext project = getProject(symbolTable);
        final List<SELayer> layers = project.getState().getLayers();
        final ExpressionNode arg = arguments.get(0);
        final Object eval = arg.evaluate(symbolTable);

        if (eval instanceof Integer index) {
            if (index < 0 || index >= layers.size()) {
                ScriptErrorLog.fireError(
                        ScriptErrorLog.Message.INDEX_OUT_OF_BOUNDS,
                        arg.getPosition(), String.valueOf(index),
                        String.valueOf(layers.size()), String.valueOf(false));
                return null;
            }

            return new LayerRep(project, index);
        } else if (eval instanceof String name) {
            for (int i = 0; i < layers.size(); i++)
                if (layers.get(i).getName().equals(name))
                    return new LayerRep(project, i);

            ScriptErrorLog.fireError(ScriptErrorLog.Message.CUSTOM_RT,
                    arg.getPosition(),
                    "No layer matching the name \"" + name + "\" was found");
        } else {
            ScriptErrorLog.fireError(ScriptErrorLog.Message.ARG_NOT_TYPE,
                    arg.getPosition(), funcName(),
                    TypeNode.getString() + " or " + TypeNode.getInt(),
                    arg.getType(symbolTable).toString());
        }

        return null;
    }
}
