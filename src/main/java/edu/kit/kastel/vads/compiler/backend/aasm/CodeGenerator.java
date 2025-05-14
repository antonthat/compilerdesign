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

import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.AddOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.ConstMovOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.MovOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.MulOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.Operation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.RetOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.SubOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.DivOperation;
import edu.kit.kastel.vads.compiler.backend.aasm.operationNodes.CDQOperation;


import edu.kit.kastel.vads.compiler.backend.regalloc.X86_64Register;


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
        return operationList;
    }

    private void generateForGraph(IrGraph graph, StringBuilder builder, Map<Node, Register> registers, List<Operation> operationList) {
        Set<Node> visited = new HashSet<>();
        scan(graph.endBlock(), visited, builder, registers, operationList);
    }

    private void scan(Node node, Set<Node> visited, StringBuilder builder, Map<Node, Register> registers, List<Operation> operationList) {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                scan(predecessor, visited, builder, registers, operationList);
            }
        }

        switch (node) {
            case AddNode add -> binary(builder, registers, add, "add",operationList);
            case SubNode sub -> binary(builder, registers, sub, "sub",operationList);
            case MulNode mul -> binary(builder, registers, mul, "mul",operationList);
            case DivNode div -> binary(builder, registers, div, "div",operationList);
            case ModNode mod -> binary(builder, registers, mod, "mod",operationList);
            case ReturnNode r -> {Operation retNode = new RetOperation(registers.get(predecessorSkipProj(r, ReturnNode.RESULT)));
                                builder.append(retNode);
                                operationList.add(retNode);
            }
            case ConstIntNode c -> {Operation constMovNode = new ConstMovOperation(registers.get(c), c.value());
                                builder.append(constMovNode);
                                operationList.add(constMovNode);}
            case Phi _ -> throw new UnsupportedOperationException("phi");
            case Block _, ProjNode _, StartNode _ -> {
                // do nothing, skip line break
                return;
            }
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
        Operation divNode;
        Operation mulNode;
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
        }
    }
}
