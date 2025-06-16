package edu.kit.kastel.vads.compiler.parser;

import edu.kit.kastel.vads.compiler.lexer.Identifier;
import edu.kit.kastel.vads.compiler.lexer.Keyword;
import edu.kit.kastel.vads.compiler.lexer.KeywordType;
import edu.kit.kastel.vads.compiler.lexer.NumberLiteral;
import edu.kit.kastel.vads.compiler.lexer.Operator;
import edu.kit.kastel.vads.compiler.lexer.Operator.OperatorType;
import edu.kit.kastel.vads.compiler.lexer.KeywordType;
import edu.kit.kastel.vads.compiler.lexer.Separator;
import edu.kit.kastel.vads.compiler.lexer.Separator.SeparatorType;
import edu.kit.kastel.vads.compiler.Span;
import edu.kit.kastel.vads.compiler.lexer.Token;
import edu.kit.kastel.vads.compiler.parser.ast.ControlTree;
import edu.kit.kastel.vads.compiler.parser.ast.BreakTree;
import edu.kit.kastel.vads.compiler.parser.ast.ContinueTree;
import edu.kit.kastel.vads.compiler.parser.ast.ForTree;
import edu.kit.kastel.vads.compiler.parser.ast.ConditionalTree;
import edu.kit.kastel.vads.compiler.parser.ast.WhileTree;
import edu.kit.kastel.vads.compiler.parser.ast.AssignmentTree;
import edu.kit.kastel.vads.compiler.parser.ast.SimpleTree;
import edu.kit.kastel.vads.compiler.parser.ast.BinaryOperationTree;
import edu.kit.kastel.vads.compiler.parser.ast.TernaryOperationTree;
import edu.kit.kastel.vads.compiler.parser.ast.BlockTree;
import edu.kit.kastel.vads.compiler.parser.ast.DeclarationTree;
import edu.kit.kastel.vads.compiler.parser.ast.ExpressionTree;
import edu.kit.kastel.vads.compiler.parser.ast.FunctionTree;
import edu.kit.kastel.vads.compiler.parser.ast.IdentExpressionTree;
import edu.kit.kastel.vads.compiler.parser.ast.LValueIdentTree;
import edu.kit.kastel.vads.compiler.parser.ast.LValueTree;
import edu.kit.kastel.vads.compiler.parser.ast.LiteralTree;
import edu.kit.kastel.vads.compiler.parser.ast.NameTree;
import edu.kit.kastel.vads.compiler.parser.ast.UnaryOperationTree;
import edu.kit.kastel.vads.compiler.parser.ast.ProgramTree;
import edu.kit.kastel.vads.compiler.parser.ast.ReturnTree;
import edu.kit.kastel.vads.compiler.parser.ast.StatementTree;
import edu.kit.kastel.vads.compiler.parser.ast.TypeTree;
import edu.kit.kastel.vads.compiler.parser.symbol.Name;
import edu.kit.kastel.vads.compiler.parser.type.BasicType;
import edu.kit.kastel.vads.compiler.parser.type.Type;
import edu.kit.kastel.vads.compiler.parser.ast.BoolLiteralTree;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final TokenSource tokenSource;

    public Parser(TokenSource tokenSource) {
        this.tokenSource = tokenSource;
    }

    public ProgramTree parseProgram() {
        ProgramTree programTree = new ProgramTree(List.of(parseFunction()));
        if (this.tokenSource.hasMore()) {
            throw new ParseException("expected end of input but got " + this.tokenSource.peek());
        }
        return programTree;
    }

    private FunctionTree parseFunction() {
        Keyword returnType = this.tokenSource.expectKeyword(KeywordType.INT);
        Identifier identifier = this.tokenSource.expectIdentifier();
        if (!identifier.value().equals("main")) {
            throw new ParseException("expected main function but got " + identifier);
        }
        this.tokenSource.expectSeparator(SeparatorType.PAREN_OPEN);
        this.tokenSource.expectSeparator(SeparatorType.PAREN_CLOSE);
        BlockTree body = parseBlock();
        return new FunctionTree(
                new TypeTree(BasicType.INT, returnType.span()),
                name(identifier),
                body
        );
    }

    private BlockTree parseBlock() {
        Separator bodyOpen = this.tokenSource.expectSeparator(SeparatorType.BRACE_OPEN);
        List<StatementTree> statements = parseStatements();
        Separator bodyClose = this.tokenSource.expectSeparator(SeparatorType.BRACE_CLOSE);
        return new BlockTree(statements, bodyOpen.span().merge(bodyClose.span()));
    }

    private List<StatementTree> parseStatements() {
        List<StatementTree> statements = new ArrayList<>();
        while (!(this.tokenSource.peek() instanceof Separator sep && sep.type() == SeparatorType.BRACE_CLOSE)) {
            statements.add(parseStatement());
        }
        return statements;
    }

    private StatementTree parseStatement() {
        StatementTree statement;
        if (this.tokenSource.peek().isControlStructure()) {
            statement = parseControl();
        }
        else if (this.tokenSource.peek() instanceof Separator sep && sep.type() == SeparatorType.BRACE_OPEN) {
            statement = parseBlock();
        } else {
            statement = parseSimple();
            this.tokenSource.expectSeparator(SeparatorType.SEMICOLON);
        }
        return statement;
    }

    private ControlTree parseIf() {
        Keyword typeIf = this.tokenSource.expectKeyword(KeywordType.IF);
        this.tokenSource.expectSeparator(SeparatorType.PAREN_OPEN);
        ExpressionTree expr = parseExpression();
        this.tokenSource.expectSeparator(SeparatorType.PAREN_CLOSE);
        StatementTree stmt1 = parseStatement();

        StatementTree stmt2 = null;
        if (this.tokenSource.peek() instanceof Keyword elseKw  && elseKw.type() == KeywordType.ELSE) {
            this.tokenSource.expectKeyword(KeywordType.ELSE);
            stmt2 = parseStatement();
        }
        Span mergeSpan = (stmt2 == null) ? stmt1.span() : stmt2.span();
        return new ConditionalTree(expr, stmt1, stmt2, typeIf.span().merge(mergeSpan));
    }

    private ControlTree parseWhile() {
        Keyword typeWhile = this.tokenSource.expectKeyword(KeywordType.WHILE);
        this.tokenSource.expectSeparator(SeparatorType.PAREN_OPEN);
        ExpressionTree expr = parseExpression();
        this.tokenSource.expectSeparator(SeparatorType.PAREN_CLOSE);
        StatementTree stmt = parseStatement();
        return new WhileTree(expr, stmt, typeWhile.span().merge(stmt.span()));
    }

    private ControlTree parseFor() {
        Keyword typeFor = this.tokenSource.expectKeyword(KeywordType.FOR);
        this.tokenSource.expectSeparator(SeparatorType.PAREN_OPEN);

        SimpleTree simp1 = null;
        if (!this.tokenSource.peek().isSeparator(SeparatorType.SEMICOLON)) {
            simp1 = parseSimple();
        }

        this.tokenSource.expectSeparator(SeparatorType.SEMICOLON);
        ExpressionTree expr = parseExpression();
        this.tokenSource.expectSeparator(SeparatorType.SEMICOLON);

        SimpleTree simp2 = null;
        if (!this.tokenSource.peek().isSeparator(SeparatorType.SEMICOLON)) {
            simp2 = parseSimple();
        }

        this.tokenSource.expectSeparator(SeparatorType.PAREN_CLOSE);

        StatementTree stmt = parseStatement();
        return new ForTree(simp1, expr, simp2, stmt, typeFor.span().merge(stmt.span()));
    }

    private ControlTree parseControl(){
        Keyword kw = (Keyword) this.tokenSource.peek();
        return switch(kw.type()) {
            case KeywordType.IF -> {
                yield parseIf();
            }
            case KeywordType.WHILE -> {
                yield parseWhile();
            }
            case KeywordType.FOR -> {
                yield parseFor();
            }
            case KeywordType.CONTINUE -> {
                Keyword type = this.tokenSource.expectKeyword(KeywordType.CONTINUE);
                this.tokenSource.expectSeparator(SeparatorType.SEMICOLON);
                yield new ContinueTree(type.span());
            }
            case KeywordType.BREAK -> {
                Keyword type = this.tokenSource.expectKeyword(KeywordType.BREAK);
                this.tokenSource.expectSeparator(SeparatorType.SEMICOLON);
                yield new BreakTree(type.span());
            }
            case KeywordType.RETURN -> {
                yield parseReturn();
            }
            default -> {throw new ParseException("Not a control token.");}
        };
    }

    private TypeTree parseType() {
        Keyword keyword = (Keyword) this.tokenSource.peek();
        this.tokenSource.expectKeyword(keyword.type());
        Type type = (keyword.type() == KeywordType.INT) ? BasicType.INT : BasicType.BOOL;
        return new TypeTree(type, keyword.span());
    }

    private SimpleTree parseDeclaration() {
        // extract type
        TypeTree typeTree = parseType();
        Identifier ident = this.tokenSource.expectIdentifier();
        ExpressionTree expr = null;
        if (this.tokenSource.peek().isOperator(OperatorType.ASSIGN)) {
            this.tokenSource.expectOperator(OperatorType.ASSIGN);
            expr = parseExpression();
        }
        return new DeclarationTree(typeTree, name(ident), expr);
    }

    private SimpleTree parseSimple() {
        if (this.tokenSource.peek().isType()) {
            return parseDeclaration();
        }
        LValueTree lValue = parseLValue();
        Operator assignmentOperator = parseAssignmentOperator();
        ExpressionTree expression = parseExpression();
        return new AssignmentTree(lValue, assignmentOperator, expression);
    }

    private Operator parseAssignmentOperator() {
        if (this.tokenSource.peek() instanceof Operator op) {
            return switch (op.type()) {
                case ASSIGN, ASSIGN_DIV, ASSIGN_MINUS, ASSIGN_MOD, ASSIGN_MUL, ASSIGN_PLUS,
                     ASSIGN_BIT_AND, ASSIGN_BIT_XOR, ASSIGN_BIT_OR, ASSIGN_LSHIFT, ASSIGN_RSHIFT  -> {
                    this.tokenSource.consume();
                    yield op;
                }
                default -> throw new ParseException("expected assignment but got " + op.type());
            };
        }
        throw new ParseException("expected assignment but got " + this.tokenSource.peek());
    }

    private LValueTree parseLValue() {
        if (this.tokenSource.peek().isSeparator(SeparatorType.PAREN_OPEN)) {
            this.tokenSource.expectSeparator(SeparatorType.PAREN_OPEN);
            LValueTree inner = parseLValue();
            this.tokenSource.expectSeparator(SeparatorType.PAREN_CLOSE);
            return inner;
        }
        Identifier identifier = this.tokenSource.expectIdentifier();
        return new LValueIdentTree(name(identifier));
    }

    private ControlTree parseReturn() {
        Keyword ret = this.tokenSource.expectKeyword(KeywordType.RETURN);
        ExpressionTree expression = parseExpression();
        this.tokenSource.expectSeparator(SeparatorType.SEMICOLON);
        return new ReturnTree(expression, ret.span().start());
    }

    private ExpressionTree parseExpression() {
        return parseTernary();
    }

    private ExpressionTree parseTernary() {
        ExpressionTree condition = parseLogicalOr();
        if (this.tokenSource.peek() instanceof Separator(var type, _) && type == SeparatorType.TERNARY) {
            this.tokenSource.expectSeparator(SeparatorType.TERNARY);
            ExpressionTree expr = parseTernary();
            this.tokenSource.expectSeparator(SeparatorType.COLON);
            ExpressionTree ternary = parseTernary();
            return new TernaryOperationTree(condition, expr, ternary);
        }
        return condition;
    }

    private ExpressionTree parseLogicalOr() {
        ExpressionTree expr = parseLogicalAnd();

        while (this.tokenSource.peek() instanceof Operator op && op.type() == OperatorType.LOGIC_OR) {
            this.tokenSource.expectOperator(OperatorType.LOGIC_OR);
            expr = new BinaryOperationTree(expr, parseLogicalAnd(), OperatorType.LOGIC_OR);
        }
        return expr;
    }

    private ExpressionTree parseLogicalAnd() {
        ExpressionTree expr = parseBitOr();

        while (this.tokenSource.peek() instanceof Operator op && op.type() == OperatorType.LOGIC_AND) {
            this.tokenSource.expectOperator(OperatorType.LOGIC_AND);
            expr = new BinaryOperationTree(expr, parseBitOr(), OperatorType.LOGIC_AND);
        }
        return expr;
    }

    private ExpressionTree parseBitOr() {
        ExpressionTree expr = parseBitXor();

        while (this.tokenSource.peek() instanceof Operator op && op.type() == OperatorType.BIT_OR) {
            this.tokenSource.expectOperator(OperatorType.BIT_OR);
            expr = new BinaryOperationTree(expr, parseBitXor(), OperatorType.BIT_OR);
        }
        return expr;
    }

    private ExpressionTree parseBitXor() {
        ExpressionTree expr = parseBitAnd();

        while (this.tokenSource.peek() instanceof Operator op && op.type() == OperatorType.BIT_XOR) {
            this.tokenSource.expectOperator(OperatorType.BIT_XOR);
            expr = new BinaryOperationTree(expr, parseBitXor(), OperatorType.BIT_XOR);
        }
        return expr;
    }

    private ExpressionTree parseBitAnd() {
        ExpressionTree expr = parseEquals();

        while (this.tokenSource.peek() instanceof Operator op && op.type() == OperatorType.BIT_AND) {
            this.tokenSource.expectOperator(OperatorType.BIT_AND);
            expr = new BinaryOperationTree(expr, parseEquals(), OperatorType.BIT_AND);
        }
        return expr;
    }

    private ExpressionTree parseEquals() {
        ExpressionTree expr = parseComparison();

        while (this.tokenSource.peek() instanceof Operator op
                && (op.type() == OperatorType.EQUALS || op.type() == OperatorType.INEQUAL)) {
            this.tokenSource.consume();
            expr = new BinaryOperationTree(expr, parseComparison(), op.type());
        }
        return expr;
    }

    private ExpressionTree parseComparison() {
        ExpressionTree expr = parseShift();

        while (this.tokenSource.peek() instanceof Operator op
                && (op.type() == OperatorType.LT || op.type() == OperatorType.GT
                    || op.type() == OperatorType.LTEQ || op.type() == OperatorType.GTEQ)) {
            this.tokenSource.consume();
            expr = new BinaryOperationTree(expr, parseShift(), op.type());
        }
        return expr;
    }

    private ExpressionTree parseShift() {
        ExpressionTree expr = parseSum();

        while (this.tokenSource.peek() instanceof Operator op
                && (op.type() == OperatorType.LSHIFT || op.type() == OperatorType.RSHIFT)) {
            this.tokenSource.consume();
            expr = new BinaryOperationTree(expr, parseSum(), op.type());
        }
        return expr;
    }

    private ExpressionTree parseSum() {
        ExpressionTree expr = parseProduct();

        while (this.tokenSource.peek() instanceof Operator op
                && (op.type() == OperatorType.PLUS || op.type() == OperatorType.MINUS)) {
            this.tokenSource.consume();
            expr = new BinaryOperationTree(expr, parseProduct(), op.type());
        }
        return expr;
    }

    private ExpressionTree parseProduct() {
        ExpressionTree expr = parseUnary();

        while (this.tokenSource.peek() instanceof Operator op
                && (op.type() == OperatorType.MUL || op.type() == OperatorType.DIV || op.type() == OperatorType.MOD)) {
            this.tokenSource.consume();
            expr = new BinaryOperationTree(expr, parseUnary(), op.type());
        }
        return expr;
    }

    private ExpressionTree parseUnary() {
        if (this.tokenSource.peek() instanceof Operator op
                && (op.type() == OperatorType.LOGIC_NOT || op.type() == OperatorType.BIT_NOT || op.type() == OperatorType.MINUS)) {
            this.tokenSource.consume();
            ExpressionTree innerExpr = parseUnary();
            ExpressionTree expr = new UnaryOperationTree(innerExpr, op.type(), op.span());
            return expr;
        }
        return parsePrimary();
    }

    private ExpressionTree parsePrimary() {
        return switch (this.tokenSource.peek()) {
            case Separator(var type, _) when type == SeparatorType.PAREN_OPEN -> {
                this.tokenSource.consume();
                ExpressionTree expression = parseExpression();
                this.tokenSource.expectSeparator(SeparatorType.PAREN_CLOSE);
                yield expression;
            }
            case Keyword(var type, _) when type == KeywordType.TRUE -> {
                Keyword trKw = this.tokenSource.expectKeyword(KeywordType.TRUE);
                yield new BoolLiteralTree("true", trKw.span());
            }
            case Keyword(var type, _) when type == KeywordType.FALSE -> {
                Keyword flKw = this.tokenSource.expectKeyword(KeywordType.FALSE);
                yield new BoolLiteralTree("false", flKw.span());
            }
            case Identifier ident -> {
                this.tokenSource.consume();
                yield new IdentExpressionTree(name(ident));
            }
            case NumberLiteral(String value, int base, Span span) -> {
                this.tokenSource.consume();
                yield new LiteralTree(value, base, span);
            }
            case Token t -> throw new ParseException("invalid factor " + t);
        };
    }

    private static NameTree name(Identifier ident) {
        return new NameTree(Name.forIdentifier(ident), ident.span());
    }
}