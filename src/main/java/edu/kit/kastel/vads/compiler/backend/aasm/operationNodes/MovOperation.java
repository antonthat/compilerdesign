package edu.kit.kastel.vads.compiler.backend.aasm.operationNodes;

import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import java.util.List;
import java.util.ArrayList;
public class MovOperation extends Operation {

    public MovOperation(Register src, Register dst) {
        this.src = src;
        this.dst = dst;
        this.instruction = "mov";
    }

    public List<Register> getUsed() {
        List<Register> used = new ArrayList();
        used.add(this.src);
        return used;
    }
}
