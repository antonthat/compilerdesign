package edu.kit.kastel.vads.compiler.backend.util;

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

public class LivenessAnalyzer {
    private Set<Register>[] live;
    private Set<Register>[] use;
    private Register[] def;
    private Set<Integer>[] succ;

    public Set<Register>[] getLive() {
        return live;
    }

    public Set<Register>[] getUse() {
        return use;
    }
    public Register[] getDef() {
        return def;
    }
    public Set<Integer>[] getSucc() {
        return succ;
    }

    public void analyze(List<Operation> operationList) {
        live = new Set[operationList.size()];
        use = new Set[operationList.size()];
        def = new Register[operationList.size()];
        succ = new Set[operationList.size()];

        // double pass enough due to missing control flow
        for (int operationIndex = operationList.size() - 1; operationIndex >= 0; operationIndex--) {
            Operation operation = operationList.get(operationIndex);
            List<Register> registerList = operation.getRegisters();

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