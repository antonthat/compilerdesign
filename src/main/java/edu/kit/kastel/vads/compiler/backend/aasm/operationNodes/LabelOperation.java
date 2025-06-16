package edu.kit.kastel.vads.compiler.backend.aasm.operationNodes;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.X86_64Register;
import java.util.List;
import java.util.ArrayList;

public class LabelOperation extends Operation {
    private String label;

    public LabelOperation(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label + ":";
    }

    @Override
    public List<Register> getRegisters() {
        List<Register> registerList = new ArrayList();
        return registerList;
    }
}
