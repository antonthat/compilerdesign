package edu.kit.kastel.vads.compiler.backend.regalloc.impl;

import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.Node;
import java.util.EnumSet;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.Operation;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.InferenceGraphConstructor;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.InferenceGraph;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.MaximumCardinality;
import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.impl.InferenceGraph;
import edu.kit.kastel.vads.compiler.backend.regalloc.X86_64Register;
import edu.kit.kastel.vads.compiler.backend.regalloc.RSPRegister;

import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.AddOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.ConstMovOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.MovOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.MulOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.Operation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.RetOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.SubOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.DivOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.CDQOperation;


public class RegisterSpilling {
    public void spillRegister(List<Operation> operationList, Set<Register> registerToSpill) {
        Map<Register, Integer> rspOffset = new HashMap<>();
        int currentOffset = 8;
        int spillage;
        Register srcSpill;
        Register dstSpill;

        Operation movNode;
        Operation movNodeHelp;
        Operation movNodeHelp2;
        Operation addNode;
        Operation subNode;
        Operation divNode;
        Operation mulNode;
        Operation retNode;

        int operationListBoundary = operationList.size();
        for (int operationSlot = 0; operationSlot < operationListBoundary; operationSlot++) {
            Operation currentOperation = operationList.get(operationSlot);
            spillage = 0;
            srcSpill = null;
            dstSpill = null;
            // checking src,dst spill
            if (registerToSpill.contains(currentOperation.getSrc())) {
                spillage += 1;
                if (!rspOffset.containsKey(currentOperation.getSrc())) {
                    rspOffset.put(currentOperation.getSrc(),currentOffset);
                    currentOffset += 8;
                }
                srcSpill = currentOperation.getSrc();
            }

            if (registerToSpill.contains(currentOperation.getDst())) {
                spillage += 1;
                if (!rspOffset.containsKey(currentOperation.getDst())) {
                    rspOffset.put(currentOperation.getDst(),currentOffset);
                    currentOffset += 8;
                }
                dstSpill = currentOperation.getDst();
            }

            // refactor maybe
            if (spillage == 1) {
                switch (operationList.get(operationSlot)) {
                    case AddOperation add -> {if (srcSpill != null) {addNode = new AddOperation(new RSPRegister(rspOffset.get(srcSpill)), add.getDst());}
                                                else {addNode = new AddOperation(add.getSrc(), new RSPRegister(rspOffset.get(dstSpill)));}
                                                operationList.set(operationSlot, addNode);
                    }
                    case SubOperation sub -> {if (srcSpill != null) {subNode = new SubOperation(new RSPRegister(rspOffset.get(srcSpill)), sub.getDst());}
                                                else {subNode = new SubOperation(sub.getSrc(), new RSPRegister(rspOffset.get(dstSpill)));}
                                                operationList.set(operationSlot, subNode);
                    }
                    case MulOperation mul ->  {if (srcSpill != null) {
                        mulNode = new MulOperation(new RSPRegister(rspOffset.get(srcSpill)), mul.getDst());
                        operationList.set(operationSlot, mulNode);}
                    else {
                        movNode = new MovOperation(new RSPRegister(rspOffset.get(dstSpill)), X86_64Register.R11);
                        mulNode = new MulOperation(mul.getSrc(), X86_64Register.R11);
                        movNodeHelp = new MovOperation(X86_64Register.R11, new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, movNode);
                        operationList.add(operationSlot + 1, mulNode);
                        operationList.add(operationSlot + 2, movNodeHelp);
                        operationListBoundary += 2;
                        operationSlot += 2;
                        }
                    }
                    case DivOperation div -> {movNode = new MovOperation(new RSPRegister(rspOffset.get(srcSpill)), X86_64Register.R11);
                                                divNode = new DivOperation(X86_64Register.R11);
                                                operationList.set(operationSlot, movNode);
                        operationList.add(operationSlot + 1, divNode);
                        operationListBoundary += 1;
                        operationSlot += 1;
                    }
                    case ConstMovOperation constMov -> {movNode = new ConstMovOperation(new RSPRegister(rspOffset.get(dstSpill)), constMov.getC());
                                                        operationList.set(operationSlot, movNode);
                    }
                    case RetOperation ret -> {retNode = new RetOperation(new RSPRegister(rspOffset.get(srcSpill)));
                                                operationList.set(operationSlot, retNode);
                    }
                    case MovOperation mov -> {if (srcSpill != null) {movNode = new MovOperation(new RSPRegister(rspOffset.get(srcSpill)), mov.getDst());}
                    else {movNode = new MovOperation(mov.getSrc(), new RSPRegister(rspOffset.get(dstSpill)));}
                        operationList.set(operationSlot, movNode);
                    }
                    default -> {}
                }
            }

            if (spillage == 2) {
                switch (operationList.get(operationSlot)) {
                    case AddOperation add -> {movNode = new MovOperation(new RSPRegister(rspOffset.get(srcSpill)), X86_64Register.R11);
                        addNode = new AddOperation(X86_64Register.R11, new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, movNode);
                        operationList.add(operationSlot + 1, addNode);
                        operationListBoundary += 1;
                        operationSlot += 1;
                    }
                    case SubOperation sub -> {movNode = new MovOperation(new RSPRegister(rspOffset.get(srcSpill)), X86_64Register.R11);
                        subNode = new SubOperation(X86_64Register.R11, new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, movNode);
                        operationList.add(operationSlot + 1, subNode);
                        operationListBoundary += 1;
                        operationSlot += 1;
                    }
                    case MulOperation mul ->  {movNode = new MovOperation(new RSPRegister(rspOffset.get(srcSpill)), X86_64Register.R11);
                        movNodeHelp = new MovOperation(new RSPRegister(rspOffset.get(dstSpill)), X86_64Register.R12);
                        mulNode = new MulOperation(X86_64Register.R11, X86_64Register.R12);
                        movNodeHelp2 = new MovOperation(X86_64Register.R12, new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, movNode);
                        operationList.add(operationSlot + 1, movNodeHelp);
                        operationList.add(operationSlot + 2, mulNode);
                        operationList.add(operationSlot + 3, movNodeHelp2);
                        operationListBoundary += 3;
                        operationSlot += 3;
                    }
                    case MovOperation mov -> {movNode = new MovOperation(new RSPRegister(rspOffset.get(srcSpill)), X86_64Register.R11);
                        movNodeHelp = new MovOperation(X86_64Register.R11, new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, movNode);
                        operationList.add(operationSlot + 1, movNodeHelp);
                        operationListBoundary += 1;
                        operationSlot += 1;
                    }
                    default -> {}
                }
            }
        }
    }
}
