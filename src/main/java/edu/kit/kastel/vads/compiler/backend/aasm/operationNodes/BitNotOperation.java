package edu.kit.kastel.vads.compiler.backend.aasm.operationNodes;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.X86_64Register;
import java.util.List;
import java.util.ArrayList;
public class BitNotOperation extends Operation {

    public BitNotOperation(Register src) {
        this.src = src;
    }
    @Override
    public String toString() {
        return "not " + src.toString();
    }

    public List<Register> getUsed() {
        List<Register> used = new ArrayList();
        used.add(this.src);
        return used;
    }

    public List<Register> getRegisters() {
        List<Register> registerList = new ArrayList();
        registerList.add(src);
        return registerList;
    }
}
