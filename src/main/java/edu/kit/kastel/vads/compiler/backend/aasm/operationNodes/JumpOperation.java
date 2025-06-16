package edu.kit.kastel.vads.compiler.backend.aasm.operationNodes;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.X86_64Register;
import java.util.List;
import java.util.ArrayList;
public class JumpOperation extends Operation {
    private String target;
    public JumpOperation(String label) {
        target = label;
    }
    @Override
    public String toString() {
        return "jmp " + this.target;
    }

    public List<Register> getUsed() {
        List<Register> used = new ArrayList();
        return used;
    }

    public List<Register> getRegisters() {
        List<Register> registerList = new ArrayList();
        return registerList;
    }

    public String getTarget() {
        return target;
    }
}
