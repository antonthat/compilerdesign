package edu.kit.kastel.vads.compiler.ir.util;

import edu.kit.kastel.vads.compiler.ir.node.Node;
import edu.kit.kastel.vads.compiler.ir.node.ProjNode;
import java.util.List;
import java.util.ArrayList;

public final class NodeSupport {
    private NodeSupport() {

    }

    public static Node predecessorSkipProj(Node node, int predIdx) {
        Node pred = node.predecessor(predIdx);
        if (pred instanceof ProjNode) {
            return pred.predecessor(ProjNode.IN);
        }
        return pred;
    }

    public static List<Node> predecessorsSkipProj(Node node) {
        List<Node> predecessors = new ArrayList<>();

        for (Node predecessor : node.predecessors()) {
            if (predecessor instanceof ProjNode) {
                predecessors.add(predecessor.predecessor(ProjNode.IN));
            } else {
                predecessors.add(predecessor);
            }
        }

        return predecessors;
    }
}
