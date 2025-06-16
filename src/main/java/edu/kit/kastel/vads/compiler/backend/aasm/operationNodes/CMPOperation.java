package edu.kit.kastel.vads.compiler.backend.aasm.operationNodes;

import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import java.util.List;
import java.util.ArrayList;

public class CMPOperation extends Operation {

    public CMPOperation(Register src, Register dst) {
        //dst is 2nd src
        this.src = src;
        this.dst = dst;
        this.instruction = "cmp";
    }

    public List<Register> getUsed() {
        List<Register> used = new ArrayList();
        used.add(this.src);
        used.add(this.dst);
        return used;
    }


}