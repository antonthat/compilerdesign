package edu.kit.kastel.vads.compiler.ir;

import edu.kit.kastel.vads.compiler.ir.node.Block;
import edu.kit.kastel.vads.compiler.ir.node.DivNode;
import edu.kit.kastel.vads.compiler.ir.node.ModNode;
import edu.kit.kastel.vads.compiler.ir.node.Node;
import edu.kit.kastel.vads.compiler.ir.optimize.Optimizer;
import edu.kit.kastel.vads.compiler.ir.util.DebugInfo;
import edu.kit.kastel.vads.compiler.ir.util.DebugInfoHelper;
import edu.kit.kastel.vads.compiler.lexer.Operator.OperatorType;
import edu.kit.kastel.vads.compiler.parser.ast.*;
import edu.kit.kastel.vads.compiler.parser.symbol.Name;
import edu.kit.kastel.vads.compiler.parser.ast.BoolLiteralTree;
import edu.kit.kastel.vads.compiler.parser.ast.ReturnTree;
import edu.kit.kastel.vads.compiler.parser.visitor.Visitor;
import edu.kit.kastel.vads.compiler.ir.node.Phi;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.function.BinaryOperator;

/// SSA translation as described in
/// [`Simple and Efficient Construction of Static Single Assignment Form`](https://compilers.cs.uni-saarland.de/papers/bbhlmz13cc.pdf).
///
/// This implementation also tracks side effect edges that can be used to avoid reordering of operations that cannot be
/// reordered.
///
/// We recommend to read the paper to better understand the mechanics implemented here.
public class SsaTranslation {
    private final FunctionTree function;
    private final GraphConstructor constructor;

    public SsaTranslation(FunctionTree function, Optimizer optimizer) {
        this.function = function;
        this.constructor = new GraphConstructor(optimizer, function.name().name().asString());
    }

    public IrGraph translate() {
        var visitor = new SsaTranslationVisitor();
        this.function.accept(visitor, this);
        new Scheduler(this.constructor.graph()).schedule();
        return this.constructor.graph();
    }

    private void writeVariable(Name variable, Block block, Node value) {
        this.constructor.writeVariable(variable, block, value);
    }

    private Node readVariable(Name variable, Block block) {
        return this.constructor.readVariable(variable, block);
    }

    private Block currentBlock() {
        return this.constructor.currentBlock();
    }

    private static class SsaTranslationVisitor implements Visitor<SsaTranslation, Optional<Node>> {

        private final Deque<LoopContext> loopStack = new ArrayDeque<>();
        // what am I doing
        private int rhsCounter = 0;
        private int mergeCounter = 0;
        private int lhsCounter = 0;
        private int trueBlockCounter = 0;
        private int falseBlockCounter = 0;
        private int whileHeadCounter = 0;
        private int whileBodyCounter = 0;
        private int whileExitCounter = 0;
        private int forHeaderCounter = 0;
        private int forBodyCounter = 0;
        private int forStepCounter = 0;
        private int forGoodByeCounter = 0;
        private int afterBreakCounter = 0;
        private int afterContinueCounter = 0;
        private int ternaryTrueCounter = 0;
        private int ternaryFalseCounter = 0;
        private int ternaryMergeCounter = 0;


        private static class LoopContext {
            final Block continueTarget;
            final Block breakTarget;

            LoopContext(Block continueTarget, Block breakTarget) {
                this.continueTarget = continueTarget;
                this.breakTarget = breakTarget;
            }
        }


        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private static final Optional<Node> NOT_AN_EXPRESSION = Optional.empty();

        private final Deque<DebugInfo> debugStack = new ArrayDeque<>();

        private void pushSpan(Tree tree) {
            this.debugStack.push(DebugInfoHelper.getDebugInfo());
            DebugInfoHelper.setDebugInfo(new DebugInfo.SourceInfo(tree.span()));
        }

        private void popSpan() {
            DebugInfoHelper.setDebugInfo(this.debugStack.pop());
        }

        @Override
        public Optional<Node> visit(AssignmentTree assignmentTree, SsaTranslation data) {
            pushSpan(assignmentTree);
            BinaryOperator<Node> desugar = switch (assignmentTree.operator().type()) {
                case ASSIGN_MINUS -> data.constructor::newSub;
                case ASSIGN_PLUS -> data.constructor::newAdd;
                case ASSIGN_MUL -> data.constructor::newMul;
                case ASSIGN_DIV -> (lhs, rhs) -> projResultDivMod(data, data.constructor.newDiv(lhs, rhs));
                case ASSIGN_MOD -> (lhs, rhs) -> projResultDivMod(data, data.constructor.newMod(lhs, rhs));
                case ASSIGN_BIT_AND -> data.constructor::newBitAnd;
                case ASSIGN_BIT_OR -> data.constructor::newBitOr;
                case ASSIGN_BIT_XOR -> data.constructor::newBitXor;
                case ASSIGN_LSHIFT -> data.constructor::newLShift;
                case ASSIGN_RSHIFT -> data.constructor::newRShift;
                case ASSIGN -> null;
                default ->
                        throw new IllegalArgumentException("not an assignment operator " + assignmentTree.operator());
            };

            switch (assignmentTree.lValue()) {
                case LValueIdentTree(var name) -> {
                    Node rhs = assignmentTree.expression().accept(this, data).orElseThrow();
                    if (desugar != null) {
                        rhs = desugar.apply(data.readVariable(name.name(), data.currentBlock()), rhs);
                    }
                    data.writeVariable(name.name(), data.currentBlock(), rhs);
                }
            }
            popSpan();
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visit(BinaryOperationTree binaryOperationTree, SsaTranslation data) {
            pushSpan(binaryOperationTree);

            // short circuit
            if (binaryOperationTree.operatorType() == OperatorType.LOGIC_AND) {
                Node res = shortCircuitAnd(binaryOperationTree, data);
                popSpan();
                return Optional.of(res);
            }

            if (binaryOperationTree.operatorType() == OperatorType.LOGIC_OR) {
                Node res = shortCircuitOr(binaryOperationTree, data);
                popSpan();
                return Optional.of(res);
            }

            Node lhs = binaryOperationTree.lhs().accept(this, data).orElseThrow();
            Node rhs = binaryOperationTree.rhs().accept(this, data).orElseThrow();
            Node res = switch (binaryOperationTree.operatorType()) {
                case MINUS -> data.constructor.newSub(lhs, rhs);
                case PLUS -> data.constructor.newAdd(lhs, rhs);
                case MUL -> data.constructor.newMul(lhs, rhs);
                case DIV -> projResultDivMod(data, data.constructor.newDiv(lhs, rhs));
                case MOD -> projResultDivMod(data, data.constructor.newMod(lhs, rhs));
                case BIT_AND -> data.constructor.newBitAnd(lhs, rhs);
                case BIT_OR -> data.constructor.newBitOr(lhs, rhs);
                case BIT_XOR -> data.constructor.newBitXor(lhs, rhs);
                case LSHIFT -> data.constructor.newLShift(lhs, rhs);
                case RSHIFT -> data.constructor.newRShift(lhs, rhs);
                case EQUALS -> data.constructor.newEquals(lhs, rhs);
                case INEQUAL -> data.constructor.newInequals(lhs, rhs); // yes that is a typo I am to lazy to fix
                case LT -> data.constructor.newLessThan(lhs, rhs);
                case GT -> data.constructor.newGreaterThan(lhs, rhs);
                case LTEQ -> data.constructor.newLessEquals(lhs, rhs);
                case GTEQ -> data.constructor.newGreaterEquals(lhs, rhs);
                default ->
                        throw new IllegalArgumentException("not a binary expression operator " + binaryOperationTree.operatorType());
            };
            popSpan();
            return Optional.of(res);
        }

        private Node shortCircuitAnd(BinaryOperationTree tree, SsaTranslation data) {
            Node lhs = tree.lhs().accept(this, data).orElseThrow();

            Block rhsBlock = data.constructor.newBlock("rhsBlock" + String.valueOf(rhsCounter++));
            Block mergeBlock = data.constructor.newBlock("mergeBlock" + String.valueOf(mergeCounter++));

            Node ifNode = data.constructor.newIfNode(lhs, rhsBlock, mergeBlock);
            Node trueProjNode = data.constructor.newIfTrueProj(ifNode);
            Node falseProjNode = data.constructor.newIfFalseProj(ifNode);

            rhsBlock.addPredecessor(trueProjNode);
            data.constructor.sealBlock(rhsBlock);
            mergeBlock.addPredecessor(falseProjNode);

            data.constructor.changeCurrentBlock(rhsBlock);
            Node rhs = tree.rhs().accept(this, data).orElseThrow();
            Node trueJump = data.constructor.newJump(mergeBlock);

            mergeBlock.addPredecessor(trueJump);
            data.constructor.sealBlock(mergeBlock);
            data.constructor.changeCurrentBlock(mergeBlock);

            Phi result = data.constructor.newPhi();
            result.appendOperand(lhs);
            result.appendOperand(rhs);

            return data.constructor.tryRemoveTrivialPhi(result);
        }

        private Node shortCircuitOr(BinaryOperationTree tree, SsaTranslation data) {
            Node lhs = tree.lhs().accept(this, data).orElseThrow();

            Block rhsBlock = data.constructor.newBlock("rhsBlock" + String.valueOf(rhsCounter++));
            Block mergeBlock = data.constructor.newBlock("mergeBlock" + String.valueOf(mergeCounter++));

            Node ifNode = data.constructor.newIfNode(lhs, mergeBlock, rhsBlock);
            Node trueProjNode = data.constructor.newIfTrueProj(ifNode);
            Node falseProjNode = data.constructor.newIfFalseProj(ifNode);

            rhsBlock.addPredecessor(falseProjNode);
            data.constructor.sealBlock(rhsBlock);
            mergeBlock.addPredecessor(trueProjNode);

            data.constructor.changeCurrentBlock(rhsBlock);
            Node rhs = tree.rhs().accept(this, data).orElseThrow();
            Node trueJump = data.constructor.newJump(mergeBlock);

            mergeBlock.addPredecessor(trueJump);
            data.constructor.sealBlock(mergeBlock);
            data.constructor.changeCurrentBlock(mergeBlock);

            Phi result = data.constructor.newPhi();
            result.addPredecessor(lhs);
            result.addPredecessor(rhs);

            return data.constructor.tryRemoveTrivialPhi(result);
        }

        @Override
        public Optional<Node> visit(BlockTree blockTree, SsaTranslation data) {
            pushSpan(blockTree);
            for (StatementTree statement : blockTree.statements()) {
                statement.accept(this, data);

                // skip all after return
                if (statement instanceof ReturnTree) {
                    break;
                }
            }
            popSpan();
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visit(DeclarationTree declarationTree, SsaTranslation data) {
            pushSpan(declarationTree);
            if (declarationTree.initializer() != null) {
                Node rhs = declarationTree.initializer().accept(this, data).orElseThrow();
                data.writeVariable(declarationTree.name().name(), data.currentBlock(), rhs);
            }
            popSpan();
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visit(FunctionTree functionTree, SsaTranslation data) {
            pushSpan(functionTree);
            Node start = data.constructor.newStart();
            data.constructor.writeCurrentSideEffect(data.constructor.newSideEffectProj(start));
            functionTree.body().accept(this, data);
            popSpan();
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visit(IdentExpressionTree identExpressionTree, SsaTranslation data) {
            pushSpan(identExpressionTree);
            Node value = data.readVariable(identExpressionTree.name().name(), data.currentBlock());
            popSpan();
            return Optional.of(value);
        }

        @Override
        public Optional<Node> visit(LiteralTree literalTree, SsaTranslation data) {
            pushSpan(literalTree);
            Node node = data.constructor.newConstInt((int) literalTree.parseValue().orElseThrow());
            popSpan();
            return Optional.of(node);
        }

        @Override
        public Optional<Node> visit(LValueIdentTree lValueIdentTree, SsaTranslation data) {
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visit(NameTree nameTree, SsaTranslation data) {
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visit(UnaryOperationTree unaryOperationTree, SsaTranslation data) {
            pushSpan(unaryOperationTree);

            Node node = unaryOperationTree.expression().accept(this, data).orElseThrow();
            Node res = null;
            switch (unaryOperationTree.operatorType()) {
                case MINUS -> {
                    res = data.constructor.newSub(data.constructor.newConstInt(0), node);
                }
                case LOGIC_NOT -> {
                    res = data.constructor.newBitXor(data.constructor.newConstInt(1), node);
                }
                case BIT_NOT -> {
                    res = data.constructor.newBitNot(node);
                }
                default -> {throw new IllegalArgumentException("Unsupported unary operator: " + unaryOperationTree.operatorType());}
            }

            popSpan();
            return Optional.of(res);
        }

        @Override
        public Optional<Node> visit(ProgramTree programTree, SsaTranslation data) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Node> visit(ReturnTree returnTree, SsaTranslation data) {
            pushSpan(returnTree);
            Node node = returnTree.expression().accept(this, data).orElseThrow();
            Node ret = data.constructor.newReturn(node);
            data.constructor.graph().endBlock().addPredecessor(ret);
            popSpan();
            return NOT_AN_EXPRESSION;
        }


        @Override
        public Optional<Node> visit(TypeTree typeTree, SsaTranslation data) {
            throw new UnsupportedOperationException();
        }


        @Override
        public Optional<Node> visit(ConditionalTree conditionalTree, SsaTranslation data) {
            pushSpan(conditionalTree);

            Node condition = conditionalTree.expr().accept(this, data).orElseThrow();

            Block thenBlock = data.constructor.newBlock("trueBlock" + String.valueOf(trueBlockCounter++));
            Block elseBlock = (conditionalTree.stmt2() != null) ? data.constructor.newBlock("falseBlock" + String.valueOf(falseBlockCounter++)) : null;
            Block mergeBlock = data.constructor.newBlock("mergeBlock" + String.valueOf(mergeCounter++));

            Node ifNode = data.constructor.newIfNode(condition, thenBlock, elseBlock == null ? mergeBlock : elseBlock);
            Node trueProjNode = data.constructor.newIfTrueProj(ifNode);
            Node falseProjNode = data.constructor.newIfFalseProj(ifNode);

            data.constructor.sealBlock(data.constructor.currentBlock());

            thenBlock.addPredecessor(trueProjNode);
            data.constructor.changeCurrentBlock(thenBlock);
            data.constructor.sealBlock(thenBlock);
            conditionalTree.stmt1().accept(this, data);
            Node thenJump = data.constructor.newJump(mergeBlock);
            data.constructor.sealBlock(data.constructor.currentBlock());
            mergeBlock.addPredecessor(thenJump);


            if (conditionalTree.stmt2() != null) {
                elseBlock.addPredecessor(falseProjNode);
                data.constructor.changeCurrentBlock(elseBlock);
                data.constructor.sealBlock(elseBlock);
                conditionalTree.stmt2().accept(this, data);
                Node elseJump = data.constructor.newJump(mergeBlock);
                data.constructor.sealBlock(data.constructor.currentBlock());
                mergeBlock.addPredecessor(elseJump);
            } else {
                mergeBlock.addPredecessor(falseProjNode);
            }

            data.constructor.changeCurrentBlock(mergeBlock);
            data.constructor.sealBlock(mergeBlock);

            popSpan();
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visit(WhileTree whileTree, SsaTranslation data) {
            pushSpan(whileTree);

            data.constructor.sealBlock(data.constructor.currentBlock());
            Block headerBlock = data.constructor.newBlock("whilehead" + String.valueOf(whileHeadCounter++));
            Block bodyBlock = data.constructor.newBlock("whilebody" + String.valueOf(whileBodyCounter++));
            Block exitBlock = data.constructor.newBlock("whileexit" + String.valueOf(whileExitCounter++));

            Node jumpToHeader = data.constructor.newJump(headerBlock);
            headerBlock.addPredecessor(jumpToHeader);
            data.constructor.changeCurrentBlock(headerBlock);

            loopStack.push(new LoopContext(headerBlock, exitBlock));

            Node condition = whileTree.expr().accept(this, data).orElseThrow();
            Node ifNode = data.constructor.newIfNode(condition, bodyBlock, exitBlock);
            Node trueProjNode = data.constructor.newIfTrueProj(ifNode);
            Node falseProjNode = data.constructor.newIfFalseProj(ifNode);
            exitBlock.addPredecessor(falseProjNode);

            bodyBlock.addPredecessor(trueProjNode);
            data.constructor.sealBlock(bodyBlock);
            data.constructor.changeCurrentBlock(bodyBlock);
            whileTree.stmt().accept(this, data);
            Node backJump = data.constructor.newJump(headerBlock);
            data.constructor.sealBlock(data.constructor.currentBlock());
            headerBlock.addPredecessor(backJump);

            loopStack.pop();

            data.constructor.sealBlock(headerBlock);
            data.constructor.sealBlock(exitBlock);
            data.constructor.changeCurrentBlock(exitBlock);

            popSpan();
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visit(ForTree forTree, SsaTranslation data) {
            pushSpan(forTree);

            if (forTree.simple1() != null) {
                forTree.simple1().accept(this, data);
            }
            data.constructor.sealBlock(data.constructor.currentBlock());

            Block headerBlock = data.constructor.newBlock("forHeader" + String.valueOf(forHeaderCounter++));
            Block bodyBlock = data.constructor.newBlock("forBody" + String.valueOf(forBodyCounter++));
            Block stepBlock = data.constructor.newBlock("forStep" + String.valueOf(forStepCounter++));
            Block exitBlock = data.constructor.newBlock("forGoodBye" + String.valueOf(forGoodByeCounter++));

            loopStack.push(new LoopContext(stepBlock, exitBlock));

            Node jumpToHeader = data.constructor.newJump(headerBlock);
            headerBlock.addPredecessor(jumpToHeader);

            data.constructor.changeCurrentBlock(headerBlock);
            Node condition = forTree.expr().accept(this, data).orElseThrow();

            Node ifNode = data.constructor.newIfNode(condition, bodyBlock, exitBlock);
            Node trueProjNode = data.constructor.newIfTrueProj(ifNode);
            Node falseProjNode = data.constructor.newIfFalseProj(ifNode);

            exitBlock.addPredecessor(falseProjNode);
            bodyBlock.addPredecessor(trueProjNode);

            data.constructor.changeCurrentBlock(stepBlock);
            if (forTree.simple2() != null) {
                forTree.simple2().accept(this, data);
            }
            Node backJump = data.constructor.newJump(headerBlock);
            headerBlock.addPredecessor(backJump);

            data.constructor.changeCurrentBlock(bodyBlock);
            forTree.stmt().accept(this, data);
            Node jumpToUpdate = data.constructor.newJump(stepBlock);
            stepBlock.addPredecessor(jumpToUpdate);

            data.constructor.sealBlock(headerBlock);
            data.constructor.sealBlock(stepBlock);
            data.constructor.sealBlock(bodyBlock);
            data.constructor.sealBlock(exitBlock);


            loopStack.pop();
            data.constructor.changeCurrentBlock(exitBlock);
            popSpan();
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visit(BreakTree breakTree, SsaTranslation data) {
            pushSpan(breakTree);

            Node breakJump = data.constructor.newJump(loopStack.peek().breakTarget);
            loopStack.peek().breakTarget.addPredecessor(breakJump);
            data.constructor.sealBlock(data.constructor.currentBlock());

            data.constructor.changeCurrentBlock(data.constructor.newBlock("afterBreak"  + String.valueOf(afterBreakCounter++)));

            popSpan();
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visit(ContinueTree continueTree, SsaTranslation data) {
            pushSpan(continueTree);

            Node continueJump = data.constructor.newJump(loopStack.peek().continueTarget);
            loopStack.peek().continueTarget.addPredecessor(continueJump);
            data.constructor.sealBlock(data.constructor.currentBlock());

            data.constructor.changeCurrentBlock(data.constructor.newBlock("afterContinue" + String.valueOf(afterContinueCounter++)));

            popSpan();
            return NOT_AN_EXPRESSION;
        }

        @Override
        public Optional<Node> visit(TernaryOperationTree ternaryTree, SsaTranslation data) {
            pushSpan(ternaryTree);

            Block trueBlock = data.constructor.newBlock("ternaryTrue"  + String.valueOf(ternaryTrueCounter++));
            Block falseBlock = data.constructor.newBlock("ternaryFalse"  + String.valueOf(ternaryFalseCounter++));
            Block mergeBlock = data.constructor.newBlock("ternarymerge"  + String.valueOf(ternaryMergeCounter++));

            Node condition = ternaryTree.lhs().accept(this, data).orElseThrow();

            Node ifNode = data.constructor.newIfNode(condition, trueBlock, falseBlock);
            Node trueProjNode = data.constructor.newIfTrueProj(ifNode);
            Node falseProjNode = data.constructor.newIfFalseProj(ifNode);

            falseBlock.addPredecessor(falseProjNode);
            trueBlock.addPredecessor(trueProjNode);
            data.constructor.sealBlock(falseBlock);
            data.constructor.sealBlock(trueBlock);

            data.constructor.changeCurrentBlock(trueBlock);
            Node trueValue = ternaryTree.mhs().accept(this, data).orElseThrow();
            Node trueJump = data.constructor.newJump(mergeBlock);
            mergeBlock.addPredecessor(trueJump);

            data.constructor.changeCurrentBlock(falseBlock);
            Node falseValue = ternaryTree.rhs().accept(this, data).orElseThrow();
            Node falseJump = data.constructor.newJump(mergeBlock);
            mergeBlock.addPredecessor(falseJump);

            data.constructor.sealBlock(mergeBlock);

            data.constructor.changeCurrentBlock(mergeBlock);
            Phi result = data.constructor.newPhi();
            result.addPredecessor(trueValue);
            result.addPredecessor(falseValue);

            popSpan();
            return Optional.of(data.constructor.tryRemoveTrivialPhi(result));
        }

        @Override
        public Optional<Node> visit(BoolLiteralTree boolTree, SsaTranslation data) {
            pushSpan(boolTree);
            Node node = data.constructor.newConstBool((boolTree.value().equals("true")) ? true : false);
            popSpan();
            return Optional.of(node);
        }

        private Node projResultDivMod(SsaTranslation data, Node divMod) {
            // make sure we actually have a div or a mod, as optimizations could
            // have changed it to something else already
            if (!(divMod instanceof DivNode || divMod instanceof ModNode)) {
                return divMod;
            }
            Node projSideEffect = data.constructor.newSideEffectProj(divMod);
            data.constructor.writeCurrentSideEffect(projSideEffect);
            return data.constructor.newResultProj(divMod);
        }
    }
}