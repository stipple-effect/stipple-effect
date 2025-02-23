package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.statement.layer;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.scripting.util.LayerRep;
import com.jordanbunke.stipple_effect.utility.StatusUpdates;
import com.jordanbunke.stipple_effect.visual.menu_elements.dialog.Textbox;

public class LayerSetNameNode extends LayerStatementNode {
    public static final String NAME = "set_name";

    public LayerSetNameNode(
            final TextPosition position, final ExpressionNode scope,
            final ExpressionNode[] args
    ) {
        super(position, scope, args, TypeNode.getString());
    }

    @Override
    protected void operation(
            final LayerRep layer, final SymbolTable symbolTable
    ) {
        final Object[] vs = arguments.evaluate(symbolTable);
        final String name = (String) vs[0];

        if (Textbox.validateAsFileName(name))
            layer.project().changeLayerName(name, layer.index());
        else
            StatusUpdates.scriptActionNotPermitted(
                    "rename the layer \"" + layer.get().getName() + "\"",
                    "\"" + name + "\" is not a valid layer name",
                    arguments.get(0).getPosition());
    }

    @Override
    protected String funcName() {
        return NAME;
    }
}
