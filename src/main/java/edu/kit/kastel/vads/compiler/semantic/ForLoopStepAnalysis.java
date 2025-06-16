package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.parser.ast.DeclarationTree;
import edu.kit.kastel.vads.compiler.parser.ast.ForTree;
import edu.kit.kastel.vads.compiler.parser.visitor.NoOpVisitor;
import edu.kit.kastel.vads.compiler.parser.visitor.Unit;

public class ForLoopStepAnalysis implements NoOpVisitor<Namespace<Void>> {

    @Override
    public Unit visit(ForTree forTree, Namespace<Void> data) {
        if (forTree.simple2() != null && forTree.simple2() instanceof DeclarationTree) {
            throw new SemanticException("Declaration inside of for loop step " +  forTree.simple2().span());
        }
        return NoOpVisitor.super.visit(forTree, data);
    }
}