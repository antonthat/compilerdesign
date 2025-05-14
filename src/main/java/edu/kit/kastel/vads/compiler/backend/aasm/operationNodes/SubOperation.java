package edu.kit.kastel.vads.compiler.backend.aasm.operationNodes;

import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import java.util.List;
import java.util.ArrayList;
public class SubOperation extends Operation {

    public SubOperation(Register src, Register dst) {
        this.src = src;
        this.dst = dst;
        this.instruction = "sub";
    }

    public List<Register> getUsed() {
        List<Register> used = new ArrayList();
        used.add(this.src);
        used.add(this.dst);
        return used;
    }
}
