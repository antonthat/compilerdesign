package edu.kit.kastel.vads.compiler.backend.regalloc.impl;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;

import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.Operation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.RetOperation;

import edu.kit.kastel.vads.compiler.backend.regalloc.impl.InferenceGraph;

public class MaximumCardinality {
    public static List<Register> generateEliminationOrdering(InferenceGraph graph) {

        Map<Register, Integer> wt = new HashMap<>();
        PriorityQueue<Register> pq = new PriorityQueue<>(
                (a, b) -> Integer.compare(wt.get(a), wt.get(b))
        );
        List<Register> eliminationOrdering = new ArrayList<>();

        Set<Register> nodes = graph.getNodes();
        for (Register node : nodes) {
            wt.put(node, 0);
            pq.add(node);
        }

        for (int i = 0; i < nodes.size(); i++) {
            Register v = pq.poll();
            eliminationOrdering.add(v);
            for (Register neighbor : graph.getNeighbors(v)) {
                wt.put(neighbor, wt.get(neighbor) + 1);
            }
        }

        return eliminationOrdering;
    }
}