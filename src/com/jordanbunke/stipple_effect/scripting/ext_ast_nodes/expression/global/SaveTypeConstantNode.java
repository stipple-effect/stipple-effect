package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.global;

import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.stipple_effect.project.SaveConfig;

public final class SaveTypeConstantNode extends ConstantNode {
    private final SaveConfig.SaveType saveType;

    public SaveTypeConstantNode(
            final TextPosition position, final SaveConfig.SaveType saveType
    ) {
        super(position, TypeNode.getInt());

        this.saveType = saveType;
    }

    @Override
    public Integer evaluate(final SymbolTable symbolTable) {
        return saveType.ordinal();
    }

    @Override
    protected String funcName() {
        return saveType.name();
    }
}
