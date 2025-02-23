package com.jordanbunke.stipple_effect.scripting.ext_ast_nodes.expression.math;

import com.jordanbunke.delta_time.scripting.ast.nodes.expression.ExpressionNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.expression.std_lib.DefFuncCallNode;
import com.jordanbunke.delta_time.scripting.ast.nodes.types.TypeNode;
import com.jordanbunke.delta_time.scripting.ast.symbol_table.SymbolTable;
import com.jordanbunke.delta_time.scripting.util.Arguments;
import com.jordanbunke.delta_time.scripting.util.TextPosition;
import com.jordanbunke.delta_time.scripting.util.TypeUtils;
import com.jordanbunke.stipple_effect.utility.Constants;

import java.util.function.DoubleFunction;

public final class MathFloatToFloatNode extends DefFuncCallNode {
    public enum Function {
        COS(Math::cos), SIN(Math::sin), TAN(Math::tan),
        ACOS(Math::acos), ASIN(Math::asin), ATAN(Math::atan),
        SQRT(Math::sqrt), ROUND(n -> (double) Math.round(n)),
        FLOOR(Math::floor), CEIL(Math::ceil);

        private final DoubleFunction<Double> f;

        Function(final DoubleFunction<Double> f) {
            this.f = f;
        }

        public double run(final double n) {
            return f.apply(n);
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private final Function func;

    public MathFloatToFloatNode(
            final TextPosition position, final Function func,
            final ExpressionNode[] args
    ) {
        super(new Arguments(args, TypeUtils.expectExact(TypeNode.getFloat())),
                TypeNode.getFloat(), position);

        this.func = func;
    }

    @Override
    public Double evaluate(final SymbolTable symbolTable) {
        final double n = (double) arguments.evaluate(symbolTable)[0];
        return func.run(n);
    }

    @Override
    protected String funcName() {
        return func.toString();
    }

    @Override
    public String toString() {
        return "$" + Constants.MATH_NAMESPACE + "." + super.toString();
    }
}
