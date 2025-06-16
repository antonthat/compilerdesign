package edu.kit.kastel.vads.compiler.ir.node;


public sealed class UnaryOperationNode extends Node permits BitNotNode {
    public static final int VAL = 0;
    protected UnaryOperationNode(Block block, Node node) {
        super(block, node);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UnaryOperationNode unOp)) {
            return false;
        }
        return obj.getClass() == this.getClass() && this.predecessor(VAL) == unOp.predecessor(VAL);
    }


    @Override
    public int hashCode() {
        return (predecessorHash(this, VAL) * 31) ^ this.getClass().hashCode();
    }
}