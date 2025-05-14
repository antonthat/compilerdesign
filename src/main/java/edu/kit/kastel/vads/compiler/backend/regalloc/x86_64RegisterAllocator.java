package edu.kit.kastel.vads.compiler.backend.regalloc;

import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.Node;
import java.util.EnumSet;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;


import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.Operation;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.InferenceGraphConstructor;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.RegisterSpilling;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.InferenceGraph;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.MaximumCardinality;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.X86_64Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.MaximumCardinality;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.GraphColorRegister;

// maybe relocate
import edu.kit.kastel.vads.compiler.backend.optimization.Optimizer;

public class x86_64RegisterAllocator {
    // duplicate
    private static final EnumSet<X86_64Register> X86_64Registers = EnumSet.allOf(X86_64Register.class);

    public String allocateRegisters(List<Operation> operationList) {
        InferenceGraph graph = new InferenceGraphConstructor().generateInferenceGraph(operationList);
        List<Register> eliminationOrdering = MaximumCardinality.generateEliminationOrdering(graph);

        // greedy coloring
        Map<Register, Integer> coloring = GraphColorRegister.colorGraph(eliminationOrdering, graph);

        // register spilling
        Set<Register> registerToSpill = new HashSet<>();
        for (Register register : coloring.keySet()) {
            if (coloring.get(register) == -2) {
                registerToSpill.add(register);
            }
        }
        new RegisterSpilling().spillRegister(operationList, registerToSpill);

        Map<Register,Register> registerMapping = new HashMap<>();
        for (Register register : coloring.keySet()) {
            // TODO BAD BAD!
            for (X86_64Register matchRegister : X86_64Registers) {
                if (coloring.get(register) == matchRegister.getColor()) {
                    registerMapping.put(register, matchRegister);
                    //System.out.println("Register: " + register + " -> " + matchRegister + " color: " + coloring.get(register));
                }
            }
        }

        StringBuilder builder = new StringBuilder();
        for (Operation op : operationList) {
            op.allocate(registerMapping);
        }

        new Optimizer().optimize(operationList);

        for (Operation op : operationList) {
            builder.append(op).append("\n");
        }
        return builder.toString();
    }

    private boolean quickContains(List<Register> l, Register reg) {
        for (Register r : l) {
            if (r.toString().equals(l.toString())) {
                return true;
            }
        }
        return false;
    }


}
