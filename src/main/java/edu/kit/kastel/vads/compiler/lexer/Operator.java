package edu.kit.kastel.vads.compiler.lexer;

import edu.kit.kastel.vads.compiler.Span;

public record Operator(OperatorType type, Span span) implements Token {

    @Override
    public boolean isOperator(OperatorType operatorType) {
        return type() == operatorType;
    }

    @Override
    public String asString() {
        return type().toString();
    }

    public enum OperatorType {
        LT("<"),
        LSHIFT("<<"),
        ASSIGN_LSHIFT("<<="),
        GT(">"),
        RSHIFT(">>"),
        ASSIGN_RSHIFT(">>="),
        ASSIGN_BIT_XOR("^="),
        BIT_XOR("^"),
        ASSIGN_BIT_OR("|"),
        BIT_OR("|"),
        LOGIC_OR("||"),
        ASSIGN_BIT_AND("&="),
        BIT_AND("&"),
        LOGIC_AND("&&"),
        EQUALS("=="),
        NOT("~"),
        ASSIGN_MINUS("-="),
        MINUS("-"),
        ASSIGN_PLUS("+="),
        PLUS("+"),
        MUL("*"),
        ASSIGN_MUL("*="),
        ASSIGN_DIV("/="),
        DIV("/"),
        ASSIGN_MOD("%="),
        MOD("%"),
        ASSIGN("="),
        ;

        private final String value;

        OperatorType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
