package edu.kit.kastel.vads.compiler.ir;

import edu.kit.kastel.vads.compiler.ir.node.Node;
import edu.kit.kastel.vads.compiler.ir.node.AddNode;
import edu.kit.kastel.vads.compiler.ir.node.Block;
import edu.kit.kastel.vads.compiler.ir.node.ConstIntNode;
import edu.kit.kastel.vads.compiler.ir.node.ConstBoolNode;
import edu.kit.kastel.vads.compiler.ir.node.DivNode;
import edu.kit.kastel.vads.compiler.ir.node.ModNode;
import edu.kit.kastel.vads.compiler.ir.node.MulNode;
import edu.kit.kastel.vads.compiler.ir.node.BitOrNode;
import edu.kit.kastel.vads.compiler.ir.node.BitAndNode;
import edu.kit.kastel.vads.compiler.ir.node.BitNotNode;
import edu.kit.kastel.vads.compiler.ir.node.BitXorNode;
import edu.kit.kastel.vads.compiler.ir.node.LShiftNode;
import edu.kit.kastel.vads.compiler.ir.node.RShiftNode;
import edu.kit.kastel.vads.compiler.ir.node.Phi;
import edu.kit.kastel.vads.compiler.ir.node.ProjNode;
import edu.kit.kastel.vads.compiler.ir.node.ReturnNode;
import edu.kit.kastel.vads.compiler.ir.node.StartNode;
import edu.kit.kastel.vads.compiler.ir.node.SubNode;
import edu.kit.kastel.vads.compiler.ir.node.EqualsNode;
import edu.kit.kastel.vads.compiler.ir.node.InequalsNode;
import edu.kit.kastel.vads.compiler.ir.node.GreaterThanNode;
import edu.kit.kastel.vads.compiler.ir.node.LessThanNode;
import edu.kit.kastel.vads.compiler.ir.node.GreaterEqualsNode;
import edu.kit.kastel.vads.compiler.ir.node.LessEqualsNode;
import edu.kit.kastel.vads.compiler.ir.node.JumpNode;
import edu.kit.kastel.vads.compiler.ir.node.IfNode;
import edu.kit.kastel.vads.compiler.ir.node.UndefNode;
import edu.kit.kastel.vads.compiler.ir.optimize.Optimizer;
import edu.kit.kastel.vads.compiler.parser.symbol.Name;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

class GraphConstructor {

    private final Optimizer optimizer;
    private final IrGraph graph;
    private final Map<Name, Map<Block, Node>> currentDef = new HashMap<>();
    private final Map<Block, Map<Name, Phi>> incompletePhis = new HashMap<>();
    private final Map<Block, Node> currentSideEffect = new HashMap<>();
    private final Map<Block, Phi> incompleteSideEffectPhis = new HashMap<>();
    private final Set<Block> sealedBlocks = new HashSet<>();
    private Block currentBlock;

    public GraphConstructor(Optimizer optimizer, String name) {
        this.optimizer = optimizer;
        this.graph = new IrGraph(name);
        this.currentBlock = this.graph.startBlock();
        // the start block never gets any more predecessors
        sealBlock(this.currentBlock);
    }

    public Block newBlock(String label) {
        return new Block(this.graph(), label);
    }

    public Node newStart() {
        assert currentBlock() == this.graph.startBlock() : "start must be in start block";
        return new StartNode(currentBlock());
    }

    public Node newAdd(Node left, Node right) {
        return this.optimizer.transform(new AddNode(currentBlock(), left, right));
    }
    public Node newSub(Node left, Node right) {
        return this.optimizer.transform(new SubNode(currentBlock(), left, right));
    }
    public Node newEquals(Node left, Node right) {
        return this.optimizer.transform(new EqualsNode(currentBlock(), left, right));
    }

    public Node newInequals(Node left, Node right) {
        return this.optimizer.transform(new InequalsNode(currentBlock(), left, right));
    }
    public Node newLessThan(Node left, Node right) {
        return this.optimizer.transform(new LessThanNode(currentBlock(), left, right));
    }

    public Node newGreaterThan(Node left, Node right) {
        return this.optimizer.transform(new GreaterThanNode(currentBlock(), left, right));
    }


    public Node newBitNot(Node node) {
        return this.optimizer.transform(new BitNotNode(currentBlock(), node));
    }

    public Node newLessEquals(Node left, Node right) {
        return this.optimizer.transform(new LessEqualsNode(currentBlock(), left, right));
    }

    public Node newGreaterEquals(Node left, Node right) {
        return this.optimizer.transform(new GreaterEqualsNode(currentBlock(), left, right));
    }

    public Node newMul(Node left, Node right) {
        return this.optimizer.transform(new MulNode(currentBlock(), left, right));
    }

    public Node newDiv(Node left, Node right) {
        return this.optimizer.transform(new DivNode(currentBlock(), left, right, readCurrentSideEffect()));
    }

    public Node newBitAnd(Node left, Node right) {
        return this.optimizer.transform(new BitAndNode(currentBlock(), left, right));
    }

    public Node newBitOr(Node left, Node right) {
        return this.optimizer.transform(new BitOrNode(currentBlock(), left, right));
    }

    public Node newBitXor(Node left, Node right) {
        return this.optimizer.transform(new BitXorNode(currentBlock(), left, right));
    }

    public Node newRShift(Node left, Node right) {
        return this.optimizer.transform(new RShiftNode(currentBlock(), left, right));
    }

    public Node newLShift(Node left, Node right) {
        return this.optimizer.transform(new LShiftNode(currentBlock(), left, right));
    }

    public Node newMod(Node left, Node right) {
        return this.optimizer.transform(new ModNode(currentBlock(), left, right, readCurrentSideEffect()));
    }

    public Node newReturn(Node result) {
        return new ReturnNode(currentBlock(), readCurrentSideEffect(), result);
    }

    public Node newConstInt(int value) {
        // always move const into start block, this allows better deduplication
        // and resultingly in better value numbering
        return this.optimizer.transform(new ConstIntNode(this.graph.startBlock(), value));
    }

    public Node newConstBool(boolean value) {
        // always move const into start block, this allows better deduplication
        // and resultingly in better value numbering
        return this.optimizer.transform(new ConstBoolNode(this.graph.startBlock(), value));
    }


    public Node newJump(Block block) {
        return this.optimizer.transform(new JumpNode(currentBlock(),block));
    }

    public Node newSideEffectProj(Node node) {
        return new ProjNode(currentBlock(), node, ProjNode.SimpleProjectionInfo.SIDE_EFFECT);
    }

    public Node newIfNode(Node node, Block thenBlock, Block elseBlock) {
        return this.optimizer.transform(new IfNode(currentBlock(), node, thenBlock, elseBlock));
    }
    public Node newIfTrueProj(Node node) {
        return new ProjNode(currentBlock(), node, ProjNode.SimpleProjectionInfo.IF_TRUE_BRANCH);
    }

    public Node newIfFalseProj(Node node) {
        return new ProjNode(currentBlock(), node, ProjNode.SimpleProjectionInfo.IF_FALSE_BRANCH);
    }

    public Node newResultProj(Node node) {
        return new ProjNode(currentBlock(), node, ProjNode.SimpleProjectionInfo.RESULT);
    }

    public Block currentBlock() {
        return this.currentBlock;
    }

    public void changeCurrentBlock(Block block) {
        this.currentBlock = block;
    }

    public Phi newPhi() {
        // don't transform phi directly, it is not ready yet
        return new Phi(currentBlock());
    }

    public IrGraph graph() {
        return this.graph;
    }

    void writeVariable(Name variable, Block block, Node value) {
        this.currentDef.computeIfAbsent(variable, _ -> new HashMap<>()).put(block, value);
    }

    Node readVariable(Name variable, Block block) {
        Node node = this.currentDef.getOrDefault(variable, Map.of()).get(block);
        if (node != null) {
            return node;
        }
        return readVariableRecursive(variable, block);
    }


    private Node readVariableRecursive(Name variable, Block block) {
        Node val;
        if (!this.sealedBlocks.contains(block)) {
            val = newPhi();
            this.incompletePhis.computeIfAbsent(block, _ -> new HashMap<>()).put(variable, (Phi) val);
        } else if (block.predecessors().size() == 1) {
            val = readVariable(variable, block.predecessors().getFirst().block());
        } else {
            val = newPhi();
            writeVariable(variable, block, val);
            val = addPhiOperands(variable, (Phi) val);
        }
        writeVariable(variable, block, val);
        return val;
    }

    Node addPhiOperands(Name variable, Phi phi) {
        for (Node pred : phi.block().predecessors()) {
            Node operand = readVariable(variable, pred.block());
            if (!(operand instanceof UndefNode)) {
                phi.appendOperand(operand);
            }
        }
        return tryRemoveTrivialPhi(phi);
    }

    // probably broken
    Node tryRemoveTrivialPhi(Phi phi) {
        List<? extends Node> preds = new ArrayList<>(phi.predecessors());
        preds.remove(phi);

        if (preds.isEmpty()) {
            return new UndefNode(currentBlock());
        } else if (preds.size() == 1) {
            Node replacement = preds.getFirst();
            for (Node succ : graph.successors(phi)) {
                for (int i = 0; i < succ.predecessors().size(); i++) {
                    if (succ.predecessors().get(i).equals(phi)) {
                        succ.setPredecessor(i, replacement);
                        if (succ instanceof Phi succPhi && sealedBlocks.contains(succ.block())) {
                            tryRemoveTrivialPhi(succPhi);
                        }
                    }
                }
            }

            return replacement;
        }
        return phi;
    }

    void sealBlock(Block block) {
        for (Map.Entry<Name, Phi> entry : this.incompletePhis.getOrDefault(block, Map.of()).entrySet()) {
            addPhiOperands(entry.getKey(), entry.getValue());
        }
        this.sealedBlocks.add(block);
    }

    public void writeCurrentSideEffect(Node node) {
        writeSideEffect(currentBlock(), node);
    }

    private void writeSideEffect(Block block, Node node) {
        this.currentSideEffect.put(block, node);
    }

    public Node readCurrentSideEffect() {
        return readSideEffect(currentBlock());
    }

    private Node readSideEffect(Block block) {
        Node node = this.currentSideEffect.get(block);
        if (node != null) {
            return node;
        }

        Phi sideEffectPhi = this.incompleteSideEffectPhis.get(block);
        if (sideEffectPhi != null) {
            addPhiOperands(sideEffectPhi);
        }

        return readSideEffectRecursive(block);
    }

    private Node readSideEffectRecursive(Block block) {
        Node val;
        if (!this.sealedBlocks.contains(block)) {
            val = newPhi();
            Phi old = this.incompleteSideEffectPhis.put(block, (Phi) val);
            assert old == null : "double readSideEffectRecursive for " + block;
        } else if (block.predecessors().size() == 1) {
            val = readSideEffect(block.predecessors().getFirst().block());
        } else {
            val = newPhi();
            writeSideEffect(block, val);
            val = addPhiOperands((Phi) val);
        }
        writeSideEffect(block, val);
        return val;
    }

    Node addPhiOperands(Phi phi) {
        for (Node pred : phi.block().predecessors()) {
            phi.appendOperand(readSideEffect(pred.block()));
        }
        return tryRemoveTrivialPhi(phi);
    }


}
