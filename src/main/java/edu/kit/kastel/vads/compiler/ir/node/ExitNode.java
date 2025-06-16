package edu.kit.kastel.vads.compiler.ir.node;

public sealed class ExitNode extends Node permits JumpNode, IfNode {
    public ExitNode(Block block, Node... predecessors) {
        super(block, predecessors);
    }
}