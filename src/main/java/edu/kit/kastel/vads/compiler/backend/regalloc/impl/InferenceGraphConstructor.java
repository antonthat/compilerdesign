package edu.kit.kastel.vads.compiler.backend.regalloc.impl;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Map;
import java.util.HashMap;

import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.Operation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.RetOperation;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.InferenceGraph;
import edu.kit.kastel.vads.compiler.backend.util.LivenessAnalyzer;

public class InferenceGraphConstructor {


    private Set<Register> nodes;
    private Map<Register, Set<Register>> edges;

    public InferenceGraph generateInferenceGraph(List<Operation> operationList) {
        // possibly refactor for dataflow analysis
        nodes = new HashSet<>();
        edges = new HashMap<>();

        for (Operation op : operationList) {
            // adding missing nodes
            for (Register register : op.getRegisters()) {
                if (!nodes.contains(register)) {
                    nodes.add(register);
                    edges.put(register, new HashSet<>());
                }
            }
        }

        LivenessAnalyzer livenessAnalyzer = new LivenessAnalyzer();
        livenessAnalyzer.analyze(operationList);

        Set<Register>[] live = livenessAnalyzer.getLive();
        Set<Register>[] use = livenessAnalyzer.getUse();
        Register[] def = livenessAnalyzer.getDef();
        Set<Integer>[] succ = livenessAnalyzer.getSucc();

        // generating edges (nodes are obfuscated in generateLivenessPredicates)
        for (int liveLine = 0; liveLine < live.length; liveLine++) {
            for (Register reg1 : live[liveLine]) {
                for (Register reg2 : live[liveLine]) {
                    edges.get(reg1).add(reg2);
                }
            }
        }
        return new InferenceGraph(nodes, edges);
    }
}