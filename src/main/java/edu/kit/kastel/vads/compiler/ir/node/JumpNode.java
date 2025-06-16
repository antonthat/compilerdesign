package edu.kit.kastel.vads.compiler.ir.node;

public final class JumpNode extends ExitNode {

    private final Block target;

    public JumpNode(Block block, Block target) {
        super(block);
        this.target = target;
    }

    public Block getTarget() {
        return target;
    }

}
