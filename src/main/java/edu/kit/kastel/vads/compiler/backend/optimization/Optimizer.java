package edu.kit.kastel.vads.compiler.backend.optimization;

import java.util.List;
import java.util.ArrayList;

import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.MovOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.Operation;
import edu.kit.kastel.vads.compiler.backend.optimization.NOPEliminator;

public class Optimizer {
    public void optimize(List<Operation> program) {
        new NOPEliminator().eliminateNOPS(program);
    }
}
