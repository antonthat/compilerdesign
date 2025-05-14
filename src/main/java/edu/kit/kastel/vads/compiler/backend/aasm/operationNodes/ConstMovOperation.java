package edu.kit.kastel.vads.compiler.backend.aasm.operationNodes;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import java.util.List;
import java.util.ArrayList;
public class ConstMovOperation extends Operation {
    private int c;

    public ConstMovOperation(Register dst, int c) {
        this.dst = dst;
        this.c = c;
    }

    @Override
    public String toString() {
        return "mov $0x" + Long.toHexString(c) + "," + dst.toString();
    }

    public List<Register> getUsed() {
        List<Register> used = new ArrayList();
        return used;
    }

    public int getC() {
        return c;
    }
}
