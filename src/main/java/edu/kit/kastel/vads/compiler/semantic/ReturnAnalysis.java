package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.parser.ast.*;
import edu.kit.kastel.vads.compiler.parser.visitor.NoOpVisitor;
import edu.kit.kastel.vads.compiler.parser.visitor.Unit;

import java.util.List;
import java.util.ArrayList;

public class ReturnAnalysis implements NoOpVisitor<ReturnAnalysis.ReturnState> {

    @Override
    public Unit visit(FunctionTree functionTree, ReturnState data) {
        if (!data.statements.contains(functionTree.body())) {
            throw new SemanticException("function " + functionTree.name() + " does not return on all control paths");
        }
        data.statements.clear();
        return NoOpVisitor.super.visit(functionTree, data);
    }

    @Override
    public Unit visit(ReturnTree returnTree, ReturnState data) {
        data.statements.add(returnTree);
        return NoOpVisitor.super.visit(returnTree, data);
    }

    @Override
    public Unit visit(BlockTree blockTree, ReturnState data) {
        List<StatementTree> blockReturns = new ArrayList<>();
        for (StatementTree statement : blockTree.statements()) {
            if (data.statements.contains(statement)) {
                blockReturns.add(statement);
            }
        }

        if (!blockReturns.isEmpty()) {
            data.statements.add(blockTree);
        }

        return NoOpVisitor.super.visit(blockTree, data);
    }


    @Override
    public Unit visit(ConditionalTree conditionalTree, ReturnState data) {
        if (data.statements.contains(conditionalTree.stmt1()) &&
                (conditionalTree.stmt2() == null || data.statements.contains(conditionalTree.stmt2()))) {
            data.statements.add(conditionalTree);
        }

        return NoOpVisitor.super.visit(conditionalTree, data);
    }

    public static class ReturnState {
        List<StatementTree> statements = new ArrayList<>();
    }
}