package edu.kit.kastel.vads.compiler.backend.aasm.operationNodes;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.X86_64Register;
import java.util.List;
import java.util.ArrayList;

public class MovzxOperation extends Operation {

    public MovzxOperation(Register src, Register dst) {
        this.src = src;
        this.dst = dst;
    }

    @Override
    public String toString() {
        return "movzx " + to8BitRegister(this.src.toString()) + ", " + this.dst;
    }

    public List<Register> getUsed() {
        List<Register> used = new ArrayList();
        used.add(src);
        return used;
    }

    public List<Register> getRegisters() {
        List<Register> registerList = new ArrayList();
        registerList.add(src);
        registerList.add(dst);
        return registerList;
    }
}

