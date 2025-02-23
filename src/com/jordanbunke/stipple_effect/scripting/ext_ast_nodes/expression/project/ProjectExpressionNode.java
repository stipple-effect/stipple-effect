package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.project;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.std_lib.MemberFuncCallNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.scripting.util.TypeUtils;
import com.jordanbunke.stipple_effect.project.SEContext;
import com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.type.ProjectTypeNode;

public abstract class ProjectExpressionNode extends MemberFuncCallNode {
    ProjectExpressionNode(
            final TextPosition position, final ExpressionNode scope,
            final TypeNode returnType,
            final ExpressionNode[] args, final TypeNode... expectedArgTypes
    ) {
        super(position, scope, ProjectTypeNode.get(),
                returnType, args, TypeUtils.expectExact(expectedArgTypes));
    }

    protected final SEContext getProject(final SymbolTable symbolTable) {
        return (SEContext) receiver.evaluate(symbolTable);
    }
}
