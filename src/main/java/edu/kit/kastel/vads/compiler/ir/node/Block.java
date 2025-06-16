package edu.kit.kastel.vads.compiler.ir.node;

import edu.kit.kastel.vads.compiler.ir.IrGraph;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public final class Block extends Node {
    private final String label;
    private Node exit = null;
    private final Map<Phi, Integer> phis = new LinkedHashMap<>();
    private Map<Phi, Integer> phiIndices = new LinkedHashMap<>();
    private List<Node> schedule = new ArrayList<>();

    public Block(IrGraph graph, String label) {
        super(graph);

        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void addPhi(Phi phi, int index) {
        this.phis.put(phi, index);
    }

    public int phiIndex(Phi phi) {
        return this.phis.get(phi);
    }

    public List<Node> schedule() {
        return new ArrayList<>(this.schedule);
    }

    public void addToSchedule(Node node) {
        this.schedule.add(node);
    }


    public void appendPhis() {
        int size = this.phiIndices.keySet().size();

        List<Phi> phis = new ArrayList<>(size);
        for (Phi phi : this.phiIndices.keySet()) {
            phis.add(phi);
        }


        List<Phi> reversedPhis = new ArrayList<>(phis);
        Collections.reverse(reversedPhis);
        this.schedule.addAll(reversedPhis);
    }

    public List<Node> getPhis() {
        return new ArrayList<>(this.phis.keySet());
    }

    public void setExit(Node exit) {
        if (this.exit != null) {
            throw new IllegalArgumentException("Second exit?");
        }

        this.exit = exit;
    }

    public void appendExit() {
        if (exit != null) {
            this.schedule.add(exit);
        }
    }

}
