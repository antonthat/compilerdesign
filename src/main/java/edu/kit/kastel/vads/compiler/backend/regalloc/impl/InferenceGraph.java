package edu.kit.kastel.vads.compiler.backend.regalloc.impl;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumSet;

import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.Operation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.RetOperation;

public class InferenceGraph {
    private Set<Register> nodes;
    private Map<Register, Set<Register>> edges;

    public InferenceGraph(Set<Register> nodes, Map<Register, Set<Register>> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public Set<Register> getNodes() {
        return nodes;
    }

    public Set<Register> getNeighbors(Register node) {
        return edges.get(node);
    }

}