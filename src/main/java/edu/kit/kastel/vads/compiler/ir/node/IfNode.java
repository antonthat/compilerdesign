package edu.kit.kastel.vads.compiler.ir.node;

public final class IfNode extends ExitNode {

    private final Block thenBlock;
    private final Block elseBlock;
    private final Node condition;

    public IfNode(Block block, Node condition, Block thenBlock, Block elseBlock) {
        super(block, condition);
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    public Node getCondition() {
        return condition;
    }
    public Block getThenBlock() {
        return thenBlock;
    }

    public Block getElseBlock() {
        return elseBlock;
    }
}
