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

import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorsSkipProj;

class Scheduler {
    private final Map<Block, Set<Node>> nodes = new HashMap<>();

    private IrGraph graph;
    public Scheduler(IrGraph graph) {
        this.graph = graph;
    }

    public void schedule() {
        Set<Node> nodes = new HashSet<>();
        nodes.add(graph.endBlock());
        collectBlock(graph.endBlock(), nodes);
        graph.addBlock(graph.endBlock());

        for (Block block : graph.getBlocks()) {
            generateBlockSchedule(block);
        }

        for (Block block : graph.getBlocks()) {
            block.appendPhis();
            block.appendExit();
        }
    }

    private void collectBlock(Block block, Set<Node> nodes) {
        for (Node next : predecessorsSkipProj(block)) {
            if (nodes.add(next)) {
                collect(next, nodes);
            }

            Block predBlock = next.block();
            if (nodes.add(predBlock)) {
                collectBlock(predBlock, nodes);
                graph.addBlock(predBlock);
            }
        }
    }

    private void collect(Node node, Set<Node> collected) {
        Block currentBlock = node.block();

        if (node instanceof JumpNode exit && !(node instanceof StartNode)) {
            currentBlock.setExit(exit);
        }
        else if (node instanceof ReturnNode exit && !(node instanceof StartNode)) {
            currentBlock.setExit(exit);

        } else {
            if (nodes.get(currentBlock) == null) {
                nodes.put(currentBlock, new HashSet<>());
            }
            nodes.get(currentBlock).add(node);
        }

        for (Node pred : node.predecessors()) {
            if (collected.add(pred)) {
                collect(pred, collected);
            }
        }
    }

    private void generateBlockSchedule(Block block) {
        Set<Node> nodesLeft = nodes.get(block);
        if (nodesLeft == null) {
            return;
        }

        while (!nodesLeft.isEmpty()) {
            Node node = nodesLeft.iterator().next();
            nodesLeft.remove(node);
            scan(node, nodesLeft);
        }
    }

    private void scan(Node node, Set<Node> nodesLeft) {
        for (Node pred : node.predecessors()) {
            if (nodesLeft.remove(pred)) {
                scan(pred, nodesLeft);
            }
        }

        switch (node) {
            case Phi phi -> {
                if (phi.isSideEffectPhi()) {
                    break;
                }

                List<Node> operands = predecessorsSkipProj(phi);
                for (int index = 0; index < operands.size(); index++) {
                    phi.block().predecessor(index).block().addPhi(phi, index);
                }
            }
            case StartNode _,ProjNode _,Block _ -> {
                break;
            }
            default -> {
                node.block().addToSchedule(node);
            }
        }
    }

}
