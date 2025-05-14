package edu.kit.kastel.vads.compiler.backend.aasm.operationNodes;

import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import java.util.List;
import java.util.ArrayList;
public class CDQOperation extends Operation {

    public CDQOperation() {
    }

    @Override
    public String toString() {
        return "cdq";
    }

    public List<Register> getUsed() {
        List<Register> used = new ArrayList();
        return used;
    }
}
