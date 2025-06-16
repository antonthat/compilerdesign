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

    public static String to8BitRegister(String reg32) {
        switch (reg32) {
            case "%eax": return "%al";
            case "%ebx": return "%bl";
            case "%ecx": return "%cl";
            case "%edx": return "%dl";
            case "%esi": return "%sil";
            case "%edi": return "%dil";
            case "%esp": return "%spl";
            case "%ebp": return "%bpl";
            case "%r8d":  return "%r8b";
            case "%r9d":  return "%r9b";
            case "%r10d": return "%r10b";
            case "%r11d": return "%r11b";
            case "%r12d": return "%r12b";
            case "%r13d": return "%r13b";
            case "%r14d": return "%r14b";
            case "%r15d": return "%r15b";
            default:
                return reg32.toString();
        }
    }
}
