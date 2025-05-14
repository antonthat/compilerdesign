package edu.kit.kastel.vads.compiler.backend.aasm.operationNodes;

import edu.kit.kastel.vads.compiler.backend.regalloc.Register;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public abstract class Operation {
    protected Register dst;
    protected Register src;
    protected String instruction;

    @Override
    public String toString() {
        return instruction + " " +  src.toString() + "," + dst.toString();
    }

    public List<Register> getRegisters() {
        List<Register> registerList = new ArrayList();
        if (dst != null) {
            registerList.add(dst);
        }
        if (src != null) {
            registerList.add(src);
        }
        return registerList;
    }

    public Register getDst() {
        return dst;
    }

    public List<Register> getUsed() { // replace with abstract
        return new ArrayList();
    };

    public Register getSrc() {
        return src;
    }

    public void allocate(Map<Register, Register> mapping) {
        if (dst != null && mapping.containsKey(dst)) {
            this.dst = mapping.get(dst);
        }
        if (src != null && mapping.containsKey(src)) {
            this.src = mapping.get(src);
        }
    }
}
