package edu.kit.kastel.vads.compiler.parser.ast;

import edu.kit.kastel.vads.compiler.Span;
import edu.kit.kastel.vads.compiler.parser.visitor.Visitor;
import edu.kit.kastel.vads.compiler.lexer.Operator.OperatorType;


public record UnaryOperationTree(ExpressionTree expression, OperatorType operatorType, Span unaryPos) implements ExpressionTree {
    @Override
    public Span span() {
        return unaryPos().merge(expression().span());
    }

    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T data) {
        return visitor.visit(this, data);
    }
}
