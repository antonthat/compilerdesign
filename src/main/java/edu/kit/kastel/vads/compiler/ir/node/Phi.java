package edu.kit.kastel.vads.compiler.ir.node;

import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorsSkipProj;

public final class Phi extends Node {
    private boolean isSideEffectPhi = false;

    public Phi(Block block) {
        super(block);
    }

    public void appendOperand(Node node) {
        addPredecessor(node);

        if (node instanceof Phi phi && isSideEffectPhi()) {
            phi.setSideEffectPhi();
        }
    }

    public boolean isSideEffectPhi() {
        return isSideEffectPhi;
    }

    public void setSideEffectPhi() {
        if (isSideEffectPhi()) {
            return;
        }

        this.isSideEffectPhi = true;
        for (Node operand : predecessorsSkipProj(this)) {
            if (operand instanceof Phi phi) {
                phi.setSideEffectPhi();
            }
        }
    }
}
