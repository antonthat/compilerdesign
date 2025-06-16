package edu.kit.kastel.vads.compiler.backend.aasm;

import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.Block;
import edu.kit.kastel.vads.compiler.ir.node.Node;
import edu.kit.kastel.vads.compiler.ir.node.ProjNode;
import edu.kit.kastel.vads.compiler.ir.node.ReturnNode;
import edu.kit.kastel.vads.compiler.ir.node.StartNode;
import edu.kit.kastel.vads.compiler.ir.node.JumpNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AasmRegisterAllocator implements RegisterAllocator {
    private int id;
    private final Map<Node, Register> registers = new HashMap<>();

    @Override
    public Map<Node, Register> allocateRegisters(IrGraph graph) {
        for (Block block : graph.getBlocks()) {
            generateForBlock(block);
        }
        return Map.copyOf(this.registers);
    }

    private void generateForGraph(IrGraph graph) {
        for (Block block : graph.getBlocks()) {
            generateForBlock(block);
        }
    }

    private void generateForBlock(Block block) {
        for (Node node : block.schedule()) {
            scan(node);
        }
        for (Node node : block.getPhis()) {
            scan(node);
        }
    }


    private void scan(Node node) {
        if (needsRegister(node)) {
            this.registers.put(node, new VirtualRegister(this.id++));
        }
    }

    private static boolean needsRegister(Node node) {
        return !(node instanceof ProjNode || node instanceof StartNode || node instanceof Block || node instanceof JumpNode);
    }
}
