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

import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.*;



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
                    case BitAndOperation bitAnd -> {
                        if (srcSpill != null) {
                            Operation bitAndNode = new BitAndOperation(new RSPRegister(rspOffset.get(srcSpill)), bitAnd.getDst());
                            operationList.set(operationSlot, bitAndNode);
                        } else {
                            Operation bitAndNode = new BitAndOperation(bitAnd.getSrc(), new RSPRegister(rspOffset.get(dstSpill)));
                            operationList.set(operationSlot, bitAndNode);
                        }
                    }
                    case BitOrOperation bitOr -> {
                        if (srcSpill != null) {
                            Operation bitOrNode = new BitOrOperation(new RSPRegister(rspOffset.get(srcSpill)), bitOr.getDst());
                            operationList.set(operationSlot, bitOrNode);
                        } else {
                            Operation bitOrNode = new BitOrOperation(bitOr.getSrc(), new RSPRegister(rspOffset.get(dstSpill)));
                            operationList.set(operationSlot, bitOrNode);
                        }
                    }
                    case BitXorOperation bitXor -> {
                        if (srcSpill != null) {
                            Operation bitXorNode = new BitXorOperation(new RSPRegister(rspOffset.get(srcSpill)), bitXor.getDst());
                            operationList.set(operationSlot, bitXorNode);
                        } else {
                            Operation bitXorNode = new BitXorOperation(bitXor.getSrc(), new RSPRegister(rspOffset.get(dstSpill)));
                            operationList.set(operationSlot, bitXorNode);
                        }
                    }
                    case BitNotOperation bitNot -> {
                        // BitNot is unary, so only dst can be spilled
                        Operation bitNotNode = new BitNotOperation(new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, bitNotNode);
                    }
                    case CMPOperation cmp -> {
                        if (srcSpill != null) {
                            Operation cmpNode = new CMPOperation(new RSPRegister(rspOffset.get(srcSpill)), cmp.getDst());
                            operationList.set(operationSlot, cmpNode);
                        } else {
                            Operation cmpNode = new CMPOperation(cmp.getSrc(), new RSPRegister(rspOffset.get(dstSpill)));
                            operationList.set(operationSlot, cmpNode);
                        }
                    }
                    case TestOperation test -> {
                        if (srcSpill != null) {
                            Operation testNode = new TestOperation(new RSPRegister(rspOffset.get(srcSpill)), test.getDst());
                            operationList.set(operationSlot, testNode);
                        } else {
                            Operation testNode = new TestOperation(test.getSrc(), new RSPRegister(rspOffset.get(dstSpill)));
                            operationList.set(operationSlot, testNode);
                        }
                    }
                    case SalOperation sal -> {
                        if (srcSpill != null) {
                            Operation salNode = new SalOperation(new RSPRegister(rspOffset.get(srcSpill)), sal.getDst());
                            operationList.set(operationSlot, salNode);
                        } else {
                            Operation salNode = new SalOperation(sal.getSrc(), new RSPRegister(rspOffset.get(dstSpill)));
                            operationList.set(operationSlot, salNode);
                        }
                    }
                    case SetEOperation setE -> {
                        Operation setENode = new SetEOperation(new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, setENode);
                    }
                    case SetNeOperation setNe -> {
                        Operation setNeNode = new SetNeOperation(new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, setNeNode);
                    }
                    case SetGOperation setG -> {
                        Operation setGNode = new SetGOperation(new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, setGNode);
                    }
                    case SetGeOperation setGe -> {
                        Operation setGeNode = new SetGeOperation(new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, setGeNode);
                    }
                    case SetLOperation setL -> {
                        Operation setLNode = new SetLOperation(new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, setLNode);
                    }
                    case SetLeOperation setLe -> {
                        Operation setLeNode = new SetLeOperation(new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, setLeNode);
                    }
                    case MovzxOperation movzx -> {
                        if (srcSpill != null) {
                            Operation movzxNode = new MovzxOperation(new RSPRegister(rspOffset.get(srcSpill)), movzx.getDst());
                            operationList.set(operationSlot, movzxNode);
                        } else {
                            Operation movzxNode = new MovzxOperation(movzx.getSrc(), new RSPRegister(rspOffset.get(dstSpill)));
                            operationList.set(operationSlot, movzxNode);
                        }
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
                    case BitAndOperation bitAnd -> {
                        movNode = new MovOperation(new RSPRegister(rspOffset.get(srcSpill)), X86_64Register.R11);
                        Operation bitAndNode = new BitAndOperation(X86_64Register.R11, new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, movNode);
                        operationList.add(operationSlot + 1, bitAndNode);
                        operationListBoundary += 1;
                        operationSlot += 1;
                    }
                    case BitOrOperation bitOr -> {
                        movNode = new MovOperation(new RSPRegister(rspOffset.get(srcSpill)), X86_64Register.R11);
                        Operation bitOrNode = new BitOrOperation(X86_64Register.R11, new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, movNode);
                        operationList.add(operationSlot + 1, bitOrNode);
                        operationListBoundary += 1;
                        operationSlot += 1;
                    }
                    case BitXorOperation bitXor -> {
                        movNode = new MovOperation(new RSPRegister(rspOffset.get(srcSpill)), X86_64Register.R11);
                        Operation bitXorNode = new BitXorOperation(X86_64Register.R11, new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, movNode);
                        operationList.add(operationSlot + 1, bitXorNode);
                        operationListBoundary += 1;
                        operationSlot += 1;
                    }
                    case CMPOperation cmp -> {
                        movNode = new MovOperation(new RSPRegister(rspOffset.get(srcSpill)), X86_64Register.R11);
                        Operation cmpNode = new CMPOperation(X86_64Register.R11, new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, movNode);
                        operationList.add(operationSlot + 1, cmpNode);
                        operationListBoundary += 1;
                        operationSlot += 1;
                    }
                    case TestOperation test -> {
                        movNode = new MovOperation(new RSPRegister(rspOffset.get(srcSpill)), X86_64Register.R11);
                        Operation testNode = new TestOperation(X86_64Register.R11, new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, movNode);
                        operationList.add(operationSlot + 1, testNode);
                        operationListBoundary += 1;
                        operationSlot += 1;
                    }
                    case SalOperation sal -> {
                        movNode = new MovOperation(new RSPRegister(rspOffset.get(srcSpill)), X86_64Register.R11);
                        Operation salNode = new SalOperation(X86_64Register.R11, new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, movNode);
                        operationList.add(operationSlot + 1, salNode);
                        operationListBoundary += 1;
                        operationSlot += 1;
                    }
                    case MovzxOperation movzx -> {
                        movNode = new MovOperation(new RSPRegister(rspOffset.get(srcSpill)), X86_64Register.R11);
                        Operation movzxNode = new MovzxOperation(X86_64Register.R11, new RSPRegister(rspOffset.get(dstSpill)));
                        operationList.set(operationSlot, movNode);
                        operationList.add(operationSlot + 1, movzxNode);
                        operationListBoundary += 1;
                        operationSlot += 1;
                    }
                    default -> {
                    }
                }
            }
        }
    }
}
