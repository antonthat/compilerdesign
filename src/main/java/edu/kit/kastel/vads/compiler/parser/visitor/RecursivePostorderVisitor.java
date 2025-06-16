package edu.kit.kastel.vads.compiler.parser.visitor;

import edu.kit.kastel.vads.compiler.parser.ast.AssignmentTree;
import edu.kit.kastel.vads.compiler.parser.ast.BinaryOperationTree;
import edu.kit.kastel.vads.compiler.parser.ast.BlockTree;
import edu.kit.kastel.vads.compiler.parser.ast.DeclarationTree;
import edu.kit.kastel.vads.compiler.parser.ast.FunctionTree;
import edu.kit.kastel.vads.compiler.parser.ast.IdentExpressionTree;
import edu.kit.kastel.vads.compiler.parser.ast.LValueIdentTree;
import edu.kit.kastel.vads.compiler.parser.ast.LiteralTree;
import edu.kit.kastel.vads.compiler.parser.ast.NameTree;
import edu.kit.kastel.vads.compiler.parser.ast.ProgramTree;
import edu.kit.kastel.vads.compiler.parser.ast.ReturnTree;
import edu.kit.kastel.vads.compiler.parser.ast.StatementTree;
import edu.kit.kastel.vads.compiler.parser.ast.TypeTree;
import edu.kit.kastel.vads.compiler.parser.ast.BoolLiteralTree;
import edu.kit.kastel.vads.compiler.parser.ast.BreakTree;
import edu.kit.kastel.vads.compiler.parser.ast.ConditionalTree;
import edu.kit.kastel.vads.compiler.parser.ast.ContinueTree;
import edu.kit.kastel.vads.compiler.parser.ast.ForTree;
import edu.kit.kastel.vads.compiler.parser.ast.TernaryOperationTree;
import edu.kit.kastel.vads.compiler.parser.ast.WhileTree;
import edu.kit.kastel.vads.compiler.parser.ast.UnaryOperationTree;


/// A visitor that traverses a tree in postorder
/// @param <T> a type for additional data
/// @param <R> a type for a return type
public class RecursivePostorderVisitor<T, R> implements Visitor<T, R> {
    private final Visitor<T, R> visitor;

    public RecursivePostorderVisitor(Visitor<T, R> visitor) {
        this.visitor = visitor;
    }

    @Override
    public R visit(AssignmentTree assignmentTree, T data) {
        R r = assignmentTree.lValue().accept(this, data);
        r = assignmentTree.expression().accept(this, accumulate(data, r));
        r = this.visitor.visit(assignmentTree, accumulate(data, r));
        return r;
    }


    @Override
    public R visit(BinaryOperationTree binaryOperationTree, T data) {
        R r = binaryOperationTree.lhs().accept(this, data);
        r = binaryOperationTree.rhs().accept(this, accumulate(data, r));
        return this.visitor.visit(binaryOperationTree, accumulate(data, r));
    }

    @Override
    public R visit(BlockTree blockTree, T data) {
        R r;
        T d = data;
        for (StatementTree statement : blockTree.statements()) {
            r = statement.accept(this, d);
            d = accumulate(d, r);
        }
        return this.visitor.visit(blockTree, d);
    }

    @Override
    public R visit(BoolLiteralTree boolLiteralTree, T data) {
        R r = this.visitor.visit(boolLiteralTree, data);
        return r;
    }

    @Override
    public R visit(BreakTree breakTree, T data) {
        R r = this.visitor.visit(breakTree, data);
        return r;
    }

    @Override
    public R visit(ConditionalTree conditionalTree, T data) {
        R r = conditionalTree.expr().accept(this, data);
        r = conditionalTree.stmt1().accept(this, accumulate(data, r));
        if (conditionalTree.stmt2() != null) {
            r = conditionalTree.stmt2().accept(this, accumulate(data, r));
        }
        r = this.visitor.visit(conditionalTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(ContinueTree continueTree, T data) {
        R r = this.visitor.visit(continueTree, data);
        return r;
    }


    @Override
    public R visit(DeclarationTree declarationTree, T data) {
        R r = declarationTree.type().accept(this, data);
        r = declarationTree.name().accept(this, accumulate(data, r));
        if (declarationTree.initializer() != null) {
            r = declarationTree.initializer().accept(this, accumulate(data, r));
        }
        r = this.visitor.visit(declarationTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(ForTree forTree, T data) {
        R r = null;
        if (forTree.simple1() != null) {
            r = forTree.simple1().accept(this, data);
        }

        if (r != null) {
            r = forTree.expr().accept(this, accumulate(data, r));
        } else {
            r = forTree.expr().accept(this, data);
        }

        if (forTree.simple2() != null) {
            r = forTree.simple2().accept(this, accumulate(data, r));
        }
        r = forTree.stmt().accept(this, accumulate(data,r));
        r = this.visitor.visit(forTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(FunctionTree functionTree, T data) {
        R r = functionTree.returnType().accept(this, data);
        r = functionTree.name().accept(this, accumulate(data, r));
        r = functionTree.body().accept(this, accumulate(data, r));
        r = this.visitor.visit(functionTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(IdentExpressionTree identExpressionTree, T data) {
        R r = identExpressionTree.name().accept(this, data);
        r = this.visitor.visit(identExpressionTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(LiteralTree literalTree, T data) {
        return this.visitor.visit(literalTree, data);
    }

    @Override
    public R visit(LValueIdentTree lValueIdentTree, T data) {
        R r = lValueIdentTree.name().accept(this, data);
        r = this.visitor.visit(lValueIdentTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(NameTree nameTree, T data) {
        return this.visitor.visit(nameTree, data);
    }

    @Override
    public R visit(ProgramTree programTree, T data) {
        R r;
        T d = data;
        for (FunctionTree tree : programTree.topLevelTrees()) {
            r = tree.accept(this, d);
            d = accumulate(data, r);
        }
        r = this.visitor.visit(programTree, d);
        return r;
    }

    @Override
    public R visit(ReturnTree returnTree, T data) {
        R r = returnTree.expression().accept(this, data);
        r = this.visitor.visit(returnTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(TypeTree typeTree, T data) {
        return this.visitor.visit(typeTree, data);
    }


    @Override
    public R visit(TernaryOperationTree ternaryOperationTree, T data) {
        R r = ternaryOperationTree.lhs().accept(this, data);
        r = ternaryOperationTree.mhs().accept(this, accumulate(data,r));
        r = ternaryOperationTree.rhs().accept(this, accumulate(data,r));
        r = this.visitor.visit(ternaryOperationTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(UnaryOperationTree unaryOperationTree, T data) {
        R r = unaryOperationTree.expression().accept(this, data);
        return this.visitor.visit(unaryOperationTree, data);
    }

    @Override
    public R visit(WhileTree whileTree, T data) {
        R r = whileTree.expr().accept(this, data);
        r = whileTree.stmt().accept(this, accumulate(data,r));
        r = this.visitor.visit(whileTree, accumulate(data, r));
        return r;
    }

    protected T accumulate(T data, R value) {
        return data;
    }
}
