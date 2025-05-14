package edu.kit.kastel.vads.compiler.backend.aasm.operationNodes;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.X86_64Register;
import java.util.List;
import java.util.ArrayList;
public class RetOperation extends Operation {

    public RetOperation(Register src) {
        this.src = src;
    }
    @Override
    public String toString() {
        return "mov " + src.toString() + ",%rax\nret";
    }

    public List<Register> getUsed() {
        List<Register> used = new ArrayList();
        used.add(this.src);
        used.add(X86_64Register.RAX);
        return used;
    }

    public List<Register> getRegisters() {
        List<Register> registerList = new ArrayList();
        registerList.add(src);
        registerList.add(X86_64Register.RAX);
        return registerList;
    }
}
