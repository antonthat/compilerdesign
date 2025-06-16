package edu.kit.kastel.vads.compiler.parser.ast;

import edu.kit.kastel.vads.compiler.Span;
import edu.kit.kastel.vads.compiler.parser.ParseException;
import edu.kit.kastel.vads.compiler.parser.visitor.Visitor;
import edu.kit.kastel.vads.compiler.semantic.SemanticException;
import java.util.Optional;
import java.util.OptionalLong;

public record BoolLiteralTree(String value, Span span) implements ExpressionTree {
    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T data) {
        return visitor.visit(this, data);
    }

}
