package edu.kit.kastel.vads.compiler.backend.regalloc;

public record RSPRegister(int offset) implements Register {

    @Override
    public String toString() {
        return String.valueOf(offset) + "(%rsp)";
    }

}