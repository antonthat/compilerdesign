package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.parser.ast.*;
import edu.kit.kastel.vads.compiler.parser.visitor.NoOpVisitor;
import edu.kit.kastel.vads.compiler.parser.visitor.RecursivePostorderVisitor;
import edu.kit.kastel.vads.compiler.parser.type.BasicType;
import edu.kit.kastel.vads.compiler.lexer.Operator.OperatorType;
import edu.kit.kastel.vads.compiler.parser.visitor.Unit;

import java.util.HashMap;
import java.util.Map;

public class TypeAnalysis implements NoOpVisitor<Namespace<BasicType>> {
    // OH NO I FORGOT THAT WE HAVE SCOPES, IF time allows. I shall edit it in.

    private final Map<ExpressionTree, BasicType> expressionTypes = new HashMap<>();

    private BasicType getExpressionType(ExpressionTree expr) {
        BasicType type = expressionTypes.get(expr);
        if (type == null) {
            throw new SemanticException("Type not determined yet for expression: " + expr);
        }
        return type;
    }

    private void setExpressionType(ExpressionTree expr, BasicType type) {
        expressionTypes.put(expr, type);
    }

    @Override
    public Unit visit(BoolLiteralTree boolLiteralTree, Namespace<BasicType> data) {
        setExpressionType(boolLiteralTree, BasicType.BOOL);
        return NoOpVisitor.super.visit(boolLiteralTree, data);
    }

    @Override
    public Unit visit(LiteralTree literalTree, Namespace<BasicType> data) {
        setExpressionType(literalTree, BasicType.INT);
        return NoOpVisitor.super.visit(literalTree, data);
    }

    @Override
    public Unit visit(IdentExpressionTree identExpressionTree, Namespace<BasicType> data) {
        NameTree varNameTree = identExpressionTree.name();
        BasicType type = data.get(varNameTree);
        if (type == null) {
            throw new SemanticException("Undefined variable: " + varNameTree.name());
        }
        setExpressionType(identExpressionTree, type);
        return NoOpVisitor.super.visit(identExpressionTree, data);
    }

    @Override
    public Unit visit(BinaryOperationTree binaryOperationTree, Namespace<BasicType> data) {
        BasicType lhsType = getExpressionType(binaryOperationTree.lhs());
        BasicType rhsType = getExpressionType(binaryOperationTree.rhs());
        OperatorType op = binaryOperationTree.operatorType();

        BasicType resultType = getBinaryOperationType(op, lhsType, rhsType);
        setExpressionType(binaryOperationTree, resultType);

        return NoOpVisitor.super.visit(binaryOperationTree, data);
    }

    @Override
    public Unit visit(UnaryOperationTree unaryOperationTree, Namespace<BasicType> data) {
        BasicType expressionType = getExpressionType(unaryOperationTree.expression());
        OperatorType op = unaryOperationTree.operatorType();

        BasicType resultType = getUnaryOperationType(op, expressionType);
        setExpressionType(unaryOperationTree, resultType);

        return NoOpVisitor.super.visit(unaryOperationTree, data);
    }

    @Override
    public Unit visit(TernaryOperationTree ternaryOperationTree, Namespace<BasicType> data) {
        BasicType conditionType = getExpressionType(ternaryOperationTree.lhs());
        BasicType trueType = getExpressionType(ternaryOperationTree.mhs());
        BasicType falseType = getExpressionType(ternaryOperationTree.rhs());

        if (conditionType != BasicType.BOOL) {
            throw new SemanticException("Ternary condition must be boolean, got: " + conditionType);
        }

        if (trueType != falseType) {
            throw new SemanticException("Ternary branches must have same type, got: " +
                    trueType + " and " + falseType);
        }

        setExpressionType(ternaryOperationTree, trueType);
        return NoOpVisitor.super.visit(ternaryOperationTree, data);
    }

    @Override
    public Unit visit(AssignmentTree assignmentTree, Namespace<BasicType> data) {
        NameTree varNameTree = ((LValueIdentTree) assignmentTree.lValue()).name();
        BasicType lvalueType = data.get(varNameTree);
        if (lvalueType == null) {
            throw new SemanticException("Undefined variable in assignment: " + varNameTree);
        }

        BasicType exprType = getExpressionType(assignmentTree.expression());

        OperatorType assignOp = assignmentTree.operator().type();
        checkValidAssignment(assignOp, lvalueType, exprType);

        return NoOpVisitor.super.visit(assignmentTree, data);
    }

    @Override
    public Unit visit(DeclarationTree declarationTree, Namespace<BasicType> data) {
        BasicType declaredType = (BasicType) declarationTree.type().type();
        NameTree varNameTree = declarationTree.name();
        data.put(varNameTree, declaredType, (existing, replacement) -> {return replacement;});

        if (declarationTree.initializer() != null) {
            BasicType initType = getExpressionType(declarationTree.initializer());
            if (declaredType != initType) {
                throw new SemanticException("Type mismatch in declaration of " + varNameTree +
                        ": expected " + declaredType + ", got " + initType);
            }
        }

        return NoOpVisitor.super.visit(declarationTree, data);
    }

    @Override
    public Unit visit(ReturnTree returnTree, Namespace<BasicType> data) {
        BasicType returnType = getExpressionType(returnTree.expression());
        // For now, assume all functions return int (main function)
        if (returnType != BasicType.INT) {
            throw new SemanticException("Return type must be int, got: " + returnType);
        }
        return NoOpVisitor.super.visit(returnTree, data);
    }

    @Override
    public Unit visit(ConditionalTree conditionalTree, Namespace<BasicType> data) {
        BasicType conditionType = getExpressionType(conditionalTree.expr());
        if (conditionType != BasicType.BOOL) {
            throw new SemanticException("Condition must be boolean, got: " + conditionType);
        }
        return NoOpVisitor.super.visit(conditionalTree, data);
    }

    @Override
    public Unit visit(WhileTree whileTree, Namespace<BasicType> data) {
        BasicType conditionType = getExpressionType(whileTree.expr());
        if (conditionType != BasicType.BOOL) {
            throw new SemanticException("While condition must be boolean, got: " + conditionType);
        }
        return NoOpVisitor.super.visit(whileTree, data);
    }

    @Override
    public Unit visit(ForTree forTree, Namespace<BasicType> data) {
        BasicType conditionType = getExpressionType(forTree.expr());
        if (conditionType != BasicType.BOOL) {
            throw new SemanticException("For condition must be boolean, got: " + conditionType);
        }
        return NoOpVisitor.super.visit(forTree, data);
    }


    private BasicType getBinaryOperationType(OperatorType op, BasicType lhs, BasicType rhs) {
        return switch (op) {
            case PLUS, MINUS, MUL, DIV, MOD -> {
                if (lhs != BasicType.INT || rhs != BasicType.INT) {
                    throw new SemanticException("Arithmetic operation requires int operands, got: " +
                            lhs + " and " + rhs);
                }
                yield BasicType.INT;
            }

            case LT, GT, LTEQ, GTEQ -> {
                if (lhs != BasicType.INT || rhs != BasicType.INT) {
                    throw new SemanticException("Comparison operation requires int operands, got: " +
                            lhs + " and " + rhs);
                }
                yield BasicType.BOOL;
            }

            case EQUALS, INEQUAL -> {
                if (lhs != rhs) {
                    throw new SemanticException("Equality operation requires same types, got: " +
                            lhs + " and " + rhs);
                }
                yield BasicType.BOOL;
            }

            case LOGIC_AND, LOGIC_OR -> {
                if (lhs != BasicType.BOOL || rhs != BasicType.BOOL) {
                    throw new SemanticException("Logical operation requires bool operands, got: " +
                            lhs + " and " + rhs);
                }
                yield BasicType.BOOL;
            }

            case BIT_AND, BIT_OR, BIT_XOR, LSHIFT, RSHIFT -> {
                if (lhs != BasicType.INT || rhs != BasicType.INT) {
                    throw new SemanticException("Bitwise operation requires int operands, got: " +
                            lhs + " and " + rhs);
                }
                yield BasicType.INT;
            }

            default -> throw new SemanticException("Invalid operation type: " + op);
        };
    }

    private BasicType getUnaryOperationType(OperatorType op, BasicType operand) {
        return switch (op) {
            case MINUS -> {
                if (operand != BasicType.INT) {
                    throw new SemanticException("Arithmetic negation requires int operand, got: " + operand);
                }
                yield BasicType.INT;
            }

            case LOGIC_NOT -> {
                if (operand != BasicType.BOOL) {
                    throw new SemanticException("Logical negation requires bool operand, got: " + operand);
                }
                yield BasicType.BOOL;
            }

            case BIT_NOT -> {
                if (operand != BasicType.INT) {
                    throw new SemanticException("Bitwise negation requires int operand, got: " + operand);
                }
                yield BasicType.INT;
            }

            default -> throw new SemanticException("Invalid operation type: " + op);
        };
    }

    private void checkValidAssignment(OperatorType assignOp, BasicType lvalueType, BasicType exprType) {
        switch (assignOp) {
            case ASSIGN -> {
                if (lvalueType != exprType) {
                    throw new SemanticException("Assignment type mismatch: cannot assign " +
                            exprType + " to " + lvalueType);
                }
            }

            case ASSIGN_PLUS, ASSIGN_MINUS, ASSIGN_MUL, ASSIGN_DIV, ASSIGN_MOD -> {
                if (lvalueType != BasicType.INT) {
                    throw new SemanticException("Arithmetic assignment requires int lvalue, got: " + lvalueType);
                }
                if (exprType != BasicType.INT) {
                    throw new SemanticException("Arithmetic assignment requires int expression, got: " + exprType);
                }
            }

            case ASSIGN_BIT_AND, ASSIGN_BIT_OR, ASSIGN_BIT_XOR, ASSIGN_LSHIFT, ASSIGN_RSHIFT -> {
                if (lvalueType != BasicType.INT) {
                    throw new SemanticException("Bitwise assignment requires int lvalue, got: " + lvalueType);
                }
                if (exprType != BasicType.INT) {
                    throw new SemanticException("Bitwise assignment requires int expression, got: " + exprType);
                }
            }

            default -> throw new SemanticException("Unknown assignment operator: " + assignOp);
        }
    }

}