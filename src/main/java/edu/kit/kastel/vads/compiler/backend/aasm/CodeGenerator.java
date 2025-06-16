package edu.kit.kastel.vads.compiler.backend.aasm;

import edu.kit.kastel.vads.compiler.backend.regalloc.Register;
import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.AddNode;
import edu.kit.kastel.vads.compiler.ir.node.BinaryOperationNode;
import edu.kit.kastel.vads.compiler.ir.node.Block;
import edu.kit.kastel.vads.compiler.ir.node.ConstIntNode;
import edu.kit.kastel.vads.compiler.ir.node.DivNode;
import edu.kit.kastel.vads.compiler.ir.node.ModNode;
import edu.kit.kastel.vads.compiler.ir.node.MulNode;
import edu.kit.kastel.vads.compiler.ir.node.Node;
import edu.kit.kastel.vads.compiler.ir.node.Phi;
import edu.kit.kastel.vads.compiler.ir.node.ProjNode;
import edu.kit.kastel.vads.compiler.ir.node.ReturnNode;
import edu.kit.kastel.vads.compiler.ir.node.StartNode;
import edu.kit.kastel.vads.compiler.ir.node.SubNode;
import edu.kit.kastel.vads.compiler.ir.node.BitOrNode;
import edu.kit.kastel.vads.compiler.ir.node.BitAndNode;
import edu.kit.kastel.vads.compiler.ir.node.BitXorNode;
import edu.kit.kastel.vads.compiler.ir.node.EqualsNode;
import edu.kit.kastel.vads.compiler.ir.node.InequalsNode;
import edu.kit.kastel.vads.compiler.ir.node.GreaterEqualsNode;
import edu.kit.kastel.vads.compiler.ir.node.GreaterThanNode;
import edu.kit.kastel.vads.compiler.ir.node.LessEqualsNode;
import edu.kit.kastel.vads.compiler.ir.node.LessThanNode;
import edu.kit.kastel.vads.compiler.ir.node.LShiftNode;
import edu.kit.kastel.vads.compiler.ir.node.RShiftNode;
import edu.kit.kastel.vads.compiler.ir.node.BitNotNode;
import edu.kit.kastel.vads.compiler.ir.node.IfNode;
import edu.kit.kastel.vads.compiler.ir.node.JumpNode;
import edu.kit.kastel.vads.compiler.ir.node.UndefNode;
import edu.kit.kastel.vads.compiler.ir.node.ConstBoolNode;


import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.AddOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.ConstMovOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.MovOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.MulOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.Operation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.RetOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.SubOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.DivOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.CDQOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.BitAndOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.BitOrOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.BitXorOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.BitNotOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.CMPOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.SalOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.SarOperation;

import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.JeOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.JumpOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.SalOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.SetEOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.SetGeOperation;

import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.SetGOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.SetLeOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.SetLOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.SetNeOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.MovzxOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.LabelOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.TestOperation;


import edu.kit.kastel.vads.compiler.backend.regalloc.X86_64Register;
import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorsSkipProj;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorSkipProj;

public class CodeGenerator {
    public List<Operation> generatePseudoAssembly(List<IrGraph> program) {
        StringBuilder builder = new StringBuilder();
        List<Operation> operationList = new ArrayList<>();
        for (IrGraph graph : program) {
            AasmRegisterAllocator allocator = new AasmRegisterAllocator();
            Map<Node, Register> registers = allocator.allocateRegisters(graph);
            generateForGraph(graph, builder, registers, operationList);
        }
        // builder for debug
        //System.out.println(builder.toString());
        return operationList;
    }

    private void generateForGraph(IrGraph graph, StringBuilder builder, Map<Node, Register> registers, List<Operation> operationList) {
        for (Block block : graph.getBlocks()) {
            generateForBlock(block, builder, registers, operationList);
        }
    }

    private void generateForBlock(Block block, StringBuilder builder, Map<Node, Register> registers, List<Operation> operationList) {
        builder.append(block.getLabel())
                .append(":\n");
        operationList.add(new LabelOperation(block.getLabel()));

        for (Node node : block.schedule()) {
            scan(block, node, builder, registers, operationList);
        }
    }


    private void scan(Block block, Node node, StringBuilder builder, Map<Node, Register> registers, List<Operation> operationList) {
        switch (node) {
            case AddNode add -> binary(builder, registers, add, "add",operationList);
            case SubNode sub -> binary(builder, registers, sub, "sub",operationList);
            case MulNode mul -> binary(builder, registers, mul, "mul",operationList);
            case DivNode div -> binary(builder, registers, div, "div",operationList);
            case ModNode mod -> binary(builder, registers, mod, "mod",operationList);
            case ReturnNode r -> {
                Operation retNode = new RetOperation(registers.get(predecessorSkipProj(r, ReturnNode.RESULT)));
                builder.append(retNode);
                operationList.add(retNode);
            }
            case ConstIntNode c -> {
                Operation constMovNode = new ConstMovOperation(registers.get(c), c.value());
                builder.append(constMovNode);
                operationList.add(constMovNode);}
            case ConstBoolNode c -> {Operation constMovNode = new ConstMovOperation(registers.get(c), c.value() ? 1 : 0);
                builder.append(constMovNode);
                operationList.add(constMovNode);}

            case BitAndNode bitAnd -> binary(builder, registers, bitAnd, "bitAnd", operationList);
            case BitOrNode bitOr -> binary(builder, registers, bitOr, "bitOr", operationList);
            case BitXorNode bitXor -> binary(builder, registers, bitXor, "bitXor", operationList);
            case EqualsNode equals -> binary(builder, registers, equals, "equals", operationList);
            case GreaterEqualsNode greaterEquals -> binary(builder, registers, greaterEquals, "greaterEquals", operationList);
            case GreaterThanNode greaterThan -> binary(builder, registers, greaterThan, "greaterThan", operationList);
            case InequalsNode inequals -> binary(builder, registers, inequals, "inequals", operationList);
            case LessEqualsNode lessEquals -> binary(builder, registers, lessEquals, "lessEquals", operationList);
            case LessThanNode lessThan -> binary(builder, registers, lessThan, "lessThan", operationList);
            case LShiftNode lShift -> binary(builder, registers, lShift, "lShift", operationList);
            case RShiftNode rShift -> binary(builder, registers, rShift, "rShift", operationList);

            case BitNotNode bitNot -> {Operation bitNotNode = new BitNotOperation(registers.get(predecessorSkipProj(bitNot, 0)));
                builder.append(bitNotNode);
                operationList.add(bitNotNode);
            }

            case IfNode ifNode -> {
                Register conditionReg = registers.get(ifNode.getCondition());

                Operation testNode = new TestOperation(conditionReg, conditionReg);
                operationList.add(testNode);

                Operation jeNode = new JeOperation(ifNode.getElseBlock().getLabel());
                operationList.add(jeNode);

                Operation jumpToThenNode = new JumpOperation(ifNode.getThenBlock().getLabel());
                operationList.add(jumpToThenNode);

                builder.append(testNode).append('\n');
                builder.append(jeNode).append('\n');
                builder.append(jumpToThenNode);
            }
            case JumpNode jump -> {
                Operation jumpNode = new JumpOperation(jump.getTarget().getLabel());
                operationList.add(jumpNode);
                builder.append(jumpNode);
            }

            case Phi phi -> {
                Node src = predecessorSkipProj(phi, block.phiIndex(phi));
                Register srcReg = registers.get(src);
                Register phiReg = registers.get(phi);

                Operation moveOp = new MovOperation(phiReg, srcReg);
                operationList.add(moveOp);
                builder.append(moveOp);

            }
            case UndefNode _ -> throw new UnsupportedOperationException("UndefNode");
            case Block _, ProjNode _, StartNode _ -> {
                // do nothing, skip line break
                return;
            }
            default -> throw new UnsupportedOperationException("Something is missing apparently.");
        }
        builder.append("\n");
    }

    private static void binary(
            StringBuilder builder,
            Map<Node, Register> registers,
            BinaryOperationNode node,
            String opcode,
            List<Operation> operationList
    ) {
        Operation movNode1;
        Operation movNode2;
        Operation addNode;
        Operation subNode;
        Operation bitAndNode;
        Operation bitOrNode;
        Operation bitXorNode;
        Operation divNode;
        Operation mulNode;
        Operation cmpNode;
        Operation sarNode;
        Operation salNode;
        Operation setNode;
        Operation movzxNode;
        Operation cdq = new CDQOperation();
        switch (opcode) {
            case "add":
                movNode1 = new MovOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        registers.get(node));
                addNode = new AddOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)),
                        registers.get(node));
                builder.append(movNode1).append('\n');
                builder.append(addNode);
                operationList.add(movNode1);
                operationList.add(addNode);
                break;
            case "sub":
                movNode1 = new MovOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        registers.get(node));
                subNode = new SubOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)),
                        registers.get(node));
                builder.append(movNode1).append('\n');
                builder.append(subNode);
                operationList.add(movNode1);
                operationList.add(subNode);
                break;
            case "mul":
                movNode1 = new MovOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        registers.get(node));
                mulNode = new MulOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)),
                        registers.get(node));
                builder.append(movNode1).append('\n');
                builder.append(mulNode);
                operationList.add(movNode1);
                operationList.add(mulNode);
                break;
            case "div":
                movNode1 = new MovOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        X86_64Register.RAX);
                divNode = new DivOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)));
                movNode2 = new MovOperation(X86_64Register.RAX, registers.get(node));
                builder.append(movNode1).append("\n").append(cdq).append("\n").append(divNode).append('\n').append(movNode2);
                operationList.add(movNode1);
                operationList.add(cdq);
                operationList.add(divNode);
                operationList.add(movNode2);
                break;
            case "mod":
                movNode1 = new MovOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        X86_64Register.RAX);

                divNode = new DivOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)));
                movNode2 = new MovOperation(X86_64Register.RDX, registers.get(node));
                builder.append(movNode1).append("\n").append(cdq).append("\n").append(divNode).append('\n').append(movNode2);
                operationList.add(movNode1);
                operationList.add(cdq);
                operationList.add(divNode);
                operationList.add(movNode2);
                break;

            case "bitAnd":
                movNode1 = new MovOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        registers.get(node));
                bitAndNode = new BitAndOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)),
                        registers.get(node));
                builder.append(movNode1).append('\n');
                builder.append(bitAndNode);
                operationList.add(movNode1);
                operationList.add(bitAndNode);
                break;
            case "bitOr":
                movNode1 = new MovOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        registers.get(node));
                bitOrNode = new BitOrOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)),
                        registers.get(node));
                builder.append(movNode1).append('\n');
                builder.append(bitOrNode);
                operationList.add(movNode1);
                operationList.add(bitOrNode);
                break;
            case "bitXor":
                movNode1 = new MovOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        registers.get(node));
                bitXorNode = new BitXorOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)),
                        registers.get(node));
                builder.append(movNode1).append('\n');
                builder.append(bitXorNode);
                operationList.add(movNode1);
                operationList.add(bitXorNode);
                break;
            case "equals":
                cmpNode = new CMPOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)));
                setNode = new SetEOperation(registers.get(node));
                movzxNode = new MovzxOperation(registers.get(node), registers.get(node));
                operationList.add(cmpNode);
                operationList.add(setNode);
                operationList.add(movzxNode);
                builder.append(cmpNode).append('\n');
                builder.append(setNode).append('\n');
                builder.append(movzxNode);
                break;

            case "inequals":

                cmpNode = new CMPOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)));
                setNode = new SetNeOperation(registers.get(node));
                movzxNode = new MovzxOperation(registers.get(node), registers.get(node));
                operationList.add(cmpNode);
                operationList.add(setNode);
                operationList.add(movzxNode);
                builder.append(cmpNode);
                builder.append(setNode).append('\n');
                builder.append(movzxNode).append('\n');
                break;

            case "greaterThan":
                cmpNode = new CMPOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)));
                setNode = new SetGOperation(registers.get(node));
                movzxNode = new MovzxOperation(registers.get(node), registers.get(node));
                operationList.add(cmpNode);
                operationList.add(setNode);
                operationList.add(movzxNode);
                builder.append(cmpNode);
                builder.append(setNode).append('\n');
                builder.append(movzxNode).append('\n');
                break;
            case "greaterEquals":
                cmpNode = new CMPOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)));
                setNode = new SetGeOperation(registers.get(node));
                movzxNode = new MovzxOperation(registers.get(node), registers.get(node));
                operationList.add(cmpNode);
                operationList.add(setNode);
                operationList.add(movzxNode);
                builder.append(cmpNode);
                builder.append(setNode).append('\n');
                builder.append(movzxNode).append('\n');
                break;

            case "lessThan":
                cmpNode = new CMPOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)));
                setNode = new SetLOperation(registers.get(node));
                movzxNode = new MovzxOperation(registers.get(node), registers.get(node));
                operationList.add(cmpNode);
                operationList.add(setNode);
                operationList.add(movzxNode);
                builder.append(cmpNode);
                builder.append(setNode).append('\n');
                builder.append(movzxNode).append('\n');
                break;

            case "lessEquals":
                cmpNode = new CMPOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)));
                setNode = new SetLeOperation(registers.get(node));
                movzxNode = new MovzxOperation(registers.get(node), registers.get(node));
                operationList.add(cmpNode);
                operationList.add(setNode);
                operationList.add(movzxNode);
                builder.append(cmpNode);
                builder.append(setNode).append('\n');
                builder.append(movzxNode).append('\n');
                break;
            case "lShift":

                movNode1 = new MovOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        registers.get(node));
                movNode2 = new MovOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)),
                        X86_64Register.RCX);
                salNode = new SalOperation(X86_64Register.CL, registers.get(node));
                operationList.add(movNode1);
                operationList.add(movNode2);
                operationList.add(salNode);
                break;


            case "rShift":
                movNode1 = new MovOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.LEFT)),
                        registers.get(node));
                movNode2 = new MovOperation(registers.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT)),
                        X86_64Register.RCX);
                sarNode = new SarOperation(X86_64Register.CL, registers.get(node));
                operationList.add(movNode1);
                operationList.add(movNode2);
                operationList.add(sarNode);
                break;

        }

    }
}