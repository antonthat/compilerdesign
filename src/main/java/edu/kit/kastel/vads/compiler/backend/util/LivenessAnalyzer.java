package edu.kit.kastel.vads.compiler.backend.util;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.Operation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.LabelOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.RetOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.JeOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.JumpOperation;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.InferenceGraph;

public class LivenessAnalyzer {
    private Set<Register>[] live;
    private Set<Register>[] use;
    private Register[] def;
    private Set<Integer>[] succ;
    private Map<String, Integer> labelToIndex;

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
        labelToIndex = new HashMap<>();

        for (int i = 0; i < operationList.size(); i++) {
            Operation op = operationList.get(i);
            if (op instanceof LabelOperation) {
                labelToIndex.put(((LabelOperation)op).toString(), i);
            }
        }

        for (int operationIndex = operationList.size() - 1; operationIndex >= 0; operationIndex--) {
            Operation operation = operationList.get(operationIndex);
            List<Register> registerList = operation.getRegisters();

            // generate use,def,succ predicate relation
            use[operationIndex] = new HashSet<>();
            use[operationIndex].addAll(operation.getUsed());
            def[operationIndex] = operation.getDst();
            succ[operationIndex] = new HashSet<>();
            buildSuccessors(operation, operationIndex, operationList);
        }


        boolean changed = true;
        while (changed) {
            changed = false;

            for (int operationIndex = operationList.size() - 1; operationIndex >= 0; operationIndex--) {
                Set<Register> oldLive = new HashSet<>(live[operationIndex] != null ? live[operationIndex] : Collections.emptySet());

                live[operationIndex] = new HashSet<>();
                live[operationIndex].addAll(use[operationIndex]);

                for (int succIndex : succ[operationIndex]) {
                    if (succIndex < operationList.size() && live[succIndex] != null) {
                        for (Register liveReg : live[succIndex]) {
                            if (!liveReg.equals(def[operationIndex])) {
                                live[operationIndex].add(liveReg);
                            }
                        }
                    }
                }

                if (!oldLive.equals(live[operationIndex])) {
                    changed = true;
                }
            }
        }

    }

    private void buildSuccessors(Operation operation, int operationIndex, List<Operation> operationList) {
        switch (operation) {
            case JumpOperation jump -> {
                Integer targetIndex = labelToIndex.get(jump.getTarget());
                if (targetIndex != null) {
                    succ[operationIndex].add(targetIndex);
                }
            }
            case JeOperation je -> {
                Integer targetIndex = labelToIndex.get(je.getTarget());
                if (targetIndex != null) {
                    succ[operationIndex].add(targetIndex);
                }
                if (operationIndex + 1 < operationList.size()) {
                    succ[operationIndex].add(operationIndex + 1);
                }
            }
            case RetOperation ret -> {
            }
            default -> {
                if (operationIndex + 1 < operationList.size()) {
                    succ[operationIndex].add(operationIndex + 1);
                }
            }
        }
    }
}