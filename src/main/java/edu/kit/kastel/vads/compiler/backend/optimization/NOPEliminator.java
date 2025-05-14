package edu.kit.kastel.vads.compiler.backend.optimization;

import java.util.List;
import java.util.ArrayList;

import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.MovOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.Operation;

public class NOPEliminator {
    public void eliminateNOPS(List<Operation> program) {
        List<Operation> mark = new ArrayList<>();
        for (Operation op: program) {
            if (op instanceof MovOperation) {
                if (op.getSrc() == op.getDst()) {
                    mark.add(op);
                }
            }
        }
        program.removeAll(mark);
    }
}
