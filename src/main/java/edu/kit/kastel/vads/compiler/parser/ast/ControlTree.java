package edu.kit.kastel.vads.compiler.parser.ast;

public sealed interface ControlTree extends StatementTree permits ConditionalTree, WhileTree, ForTree, ContinueTree, BreakTree, ReturnTree  {
}
