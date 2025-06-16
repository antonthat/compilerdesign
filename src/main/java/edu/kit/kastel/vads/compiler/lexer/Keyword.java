package edu.kit.kastel.vads.compiler.lexer;

import edu.kit.kastel.vads.compiler.Span;

public record Keyword(KeywordType type, Span span) implements Token {
    @Override
    public boolean isKeyword(KeywordType keywordType) {
        return type() == keywordType;
    }

    @Override
    public String asString() {
        return type().keyword();
    }

    @Override
    public boolean isType() {
        return type().keyword().equals("int") || type().keyword().equals("bool");
    }

    @Override
    public boolean isControlStructure() {
        return type().keyword().equals("if")
                || type().keyword().equals("while")
                || type().keyword().equals("for")
                || type().keyword().equals("continue")
                || type().keyword().equals("break")
                || type().keyword().equals("return");
    }
}
