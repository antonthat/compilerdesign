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

public class InferenceGraphConstructor {


    private Set<Register> nodes;
    private Map<Register, Set<Register>> edges;
    private Set<Register>[] live;
    private Set<Register>[] use;
    private Register[] def;
    private Set<Integer>[] succ;

    public InferenceGraph generateInferenceGraph(List<Operation> operationList) {
        // possibly refactor for dataflow analysis
        nodes = new HashSet<>();
        edges = new HashMap<>();
        live = new Set[operationList.size()];
        use = new Set[operationList.size()];
        def = new Register[operationList.size()];
        succ = new Set[operationList.size()];

        generateLivenessPredicates(operationList);

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


    private void generateLivenessPredicates(List<Operation> operationList) {
        // double pass enough due to missing control flow
        for (int operationIndex = operationList.size() - 1; operationIndex >= 0; operationIndex--) {
            Operation operation = operationList.get(operationIndex);
            List<Register> registerList = operation.getRegisters();

            // adding missing nodes
            for (Register register : registerList) {
                if (!nodes.contains(register)) {
                    nodes.add(register);
                    edges.put(register, new HashSet<>());
                }
            }

            // generate use,def,succ predicate relation
            use[operationIndex] = new HashSet<>();
            use[operationIndex].addAll(operation.getUsed());
            def[operationIndex] = operation.getDst();
            succ[operationIndex] = new HashSet<>();
            // TODO change for control flow goto
            if (!(operation instanceof RetOperation)) {
                succ[operationIndex].add(operationIndex + 1);
            }
        }

        // second pass for liveness extraction
        for (int operationIndex = operationList.size() - 1; operationIndex >= 0; operationIndex--) {
            live[operationIndex] = new HashSet<>();
            for (Register liveReg : use[operationIndex]) {
                live[operationIndex].add(liveReg);
            }

            for (int succIndex : succ[operationIndex]) {
                Operation succOp = operationList.get(succIndex);
                for (Register liveReg : live[succIndex]) {
                    if (def[operationIndex] != liveReg) {
                        live[operationIndex].add(liveReg);
                    }
                }
            }
        }
    }

}