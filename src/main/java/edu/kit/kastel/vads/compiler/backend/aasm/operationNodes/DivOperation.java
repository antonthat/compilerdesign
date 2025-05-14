package edu.kit.kastel.vads.compiler.backend.aasm.operationNodes;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.X86_64Register;
import java.util.List;
import java.util.ArrayList;

public class DivOperation extends Operation {
    private Register divisor;
    public DivOperation(Register divisor) {
        this.src = divisor;
        this.instruction = "idiv";
    }

    @Override
    public String toString() {
        return "idiv " + src.toString();
    }

    public List<Register> getUsed() {
        List<Register> used = new ArrayList();
        used.add(this.src);
        used.add(X86_64Register.RAX);
        used.add(X86_64Register.RDX);
        return used;
    }

    public List<Register> getRegisters() {
        List<Register> registerList = new ArrayList();
        registerList.add(src);
        registerList.add(X86_64Register.RAX);
        registerList.add(X86_64Register.RDX);
        return registerList;
    }
}
