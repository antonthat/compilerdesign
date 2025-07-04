package edu.kit.kastel.vads.compiler.backend.aasm.operationNodes;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.X86_64Register;
import java.util.List;
import java.util.ArrayList;

public class SetGeOperation extends Operation {
    public SetGeOperation(Register dst) {
        this.dst = dst;
    }

    @Override
    public String toString() {
        return "setge " + to8BitRegister(this.dst.toString());
    }

    public List<Register> getUsed() {
        List<Register> used = new ArrayList();
        return used;
    }

    public List<Register> getRegisters() {
        List<Register> registerList = new ArrayList();
        registerList.add(dst);
        return registerList;
    }
}
