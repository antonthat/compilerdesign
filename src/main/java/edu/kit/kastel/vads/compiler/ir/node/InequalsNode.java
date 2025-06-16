package edu.kit.kastel.vads.compiler.ir.node;

public final class InequalsNode extends BinaryOperationNode {

    public InequalsNode(Block block, Node left, Node right) {
        super(block, left, right);
    }

    @SuppressWarnings("EqualsDoesntCheckParameterClass") // we do, but not here
    @Override
    public boolean equals(Object obj) {
        return commutativeEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return commutativeHashCode(this);
    }
}
