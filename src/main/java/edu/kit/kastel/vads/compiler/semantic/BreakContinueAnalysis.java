package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.Span;
import edu.kit.kastel.vads.compiler.parser.visitor.NoOpVisitor;
import edu.kit.kastel.vads.compiler.parser.visitor.Unit;
import edu.kit.kastel.vads.compiler.parser.ast.ForTree;
import edu.kit.kastel.vads.compiler.parser.ast.WhileTree;
import edu.kit.kastel.vads.compiler.parser.ast.BreakTree;
import edu.kit.kastel.vads.compiler.parser.ast.ContinueTree;
import edu.kit.kastel.vads.compiler.parser.ast.FunctionTree;
import edu.kit.kastel.vads.compiler.parser.ast.ControlTree;
import edu.kit.kastel.vads.compiler.Position;

import java.util.List;
import java.util.ArrayList;

public class BreakContinueAnalysis implements NoOpVisitor<BreakContinueAnalysis.JumpState> {

    @Override
    public Unit visit(BreakTree breakTree, JumpState data) {
        data.jumps.add(breakTree);
        return NoOpVisitor.super.visit(breakTree, data);
    }

    @Override
    public Unit visit(ContinueTree continueTree, JumpState data) {
        data.jumps.add(continueTree);
        return NoOpVisitor.super.visit(continueTree, data);
    }

    @Override
    public Unit visit(WhileTree whileTree, JumpState data) {
        verifyJump(whileTree, data);
        return NoOpVisitor.super.visit(whileTree, data);
    }

    @Override
    public Unit visit(FunctionTree functionTree, JumpState data) {
        if (!data.jumps.isEmpty()) {
            ControlTree firstIllegalJump = data.jumps.get(0);
            throw new SemanticException("Illegal jump statement at " + firstIllegalJump.span());
        }
        return NoOpVisitor.super.visit(functionTree, data);
    }

    @Override
    public Unit visit(ForTree forTree, JumpState data) {
        verifyJump(forTree, data);
        return NoOpVisitor.super.visit(forTree, data);
    }

    private void verifyJump(ControlTree controlTree, JumpState data) {
        data.jumps.removeIf(jump -> isWithinSpan(jump.span(), controlTree.span()));
    }

    private boolean isWithinSpan(Span jumpSpan, Span loopSpan) {
        Position jumpStart = jumpSpan.start();
        Position jumpEnd = jumpSpan.end();
        Position loopStart = loopSpan.start();
        Position loopEnd = loopSpan.end();

        // Check if jump is completely within the loop span
        if (jumpStart.line() < loopStart.line() || jumpStart.line() > loopEnd.line()) {
            return false;
        }
        if ((jumpStart.line() == loopStart.line() && jumpStart.column() < loopStart.column()) ||
                (jumpEnd.line() == loopEnd.line() && jumpEnd.column() > loopEnd.column())) {
            return false;
        }
        return true;
    }


    static class JumpState {
        List<ControlTree> jumps = new ArrayList<>();
    }
}