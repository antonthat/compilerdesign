package edu.kit.kastel.vads.compiler.backend.regalloc;


public enum X86_64Register implements Register {
    RAX("rax",0),
    RBX("rbx",4),
    RCX("rcx",5),
    RDX("rdx",1),
    RSI("rsi",6),
    RDI("rdi",7),
    RBP("rbp",-1),
    RSP("rsp",-1),
    R8("r8",8),
    R9("r9",9),
    R10("r10",10),
    R11("r11",2),
    R12("r12",3),
    R13("r13",11),
    R14("r14",12),
    R15("r15",13);

  String name;
  int color;
    X86_64Register(String name, int color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public String toString() {
        return "%" + this.name;
    }

    public int getColor() {
        return color;
    }
}