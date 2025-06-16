package edu.kit.kastel.vads.compiler.parser.ast;

import edu.kit.kastel.vads.compiler.Position;
import edu.kit.kastel.vads.compiler.Span;
import edu.kit.kastel.vads.compiler.parser.visitor.Visitor;

public record ConditionalTree(ExpressionTree expr, StatementTree stmt1, StatementTree stmt2, Span span) implements ControlTree {

    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T data) {
        return visitor.visit(this, data);
    }
}
