package edu.kit.kastel.vads.compiler.backend.regalloc.impl;

import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.Node;
import java.util.EnumSet;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.Operation;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.InferenceGraphConstructor;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.InferenceGraph;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.MaximumCardinality;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.InferenceGraph;
import edu.kit.kastel.vads.compiler.backend.regalloc.X86_64Register;


public class GraphColorRegister {
    private static final EnumSet<X86_64Register> specialRegisters = EnumSet.of(X86_64Register.RAX,
                                                                                X86_64Register.RDX,
                                                                                X86_64Register.RBP,
                                                                                X86_64Register.RSP,
                                                                                X86_64Register.R11,
                                                                                X86_64Register.R12);

    public static Map<Register, Integer> colorGraph(List<Register> eliminationOrdering, InferenceGraph graph) {
        Map<Register, Integer> color = new HashMap<>();
        // reorder special registers
        for (X86_64Register register : specialRegisters) {
            if (eliminationOrdering.contains(register)) {
                color.put(register, register.getColor());
                //reshuffle
                eliminationOrdering.remove(register);
                eliminationOrdering.add(register);
            }
        }

        for (Register vi : eliminationOrdering) {
            // skip specialReg
            if (specialRegisters.contains(vi)) {
                continue;
            }
            // lowest color
            Set<Integer> usedColors = new HashSet<>();
            for (Register neighbor: graph.getNeighbors(vi)) {
                if (color.containsKey(neighbor)) {
                    usedColors.add(color.get(neighbor));
                }
            }
            int lowestAvailable = 0;
            while (usedColors.contains(lowestAvailable)) {
                lowestAvailable++;
            }
            if (lowestAvailable > 13) {
                color.put(vi, -2);
            } else {
                color.put(vi, lowestAvailable);
            }
        }
        return color;
    }

}
