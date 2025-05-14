package edu.kit.kastel.vads.compiler.backend.regalloc;


public enum X86_64Register implements Register {
    RAX("eax",0),
    RBX("ebx",4),
    RCX("ecx",5),
    RDX("edx",1),
    RSI("esi",6),
    RDI("edi",7),
    RBP("ebp",-1),
    RSP("esp",-1),
    R8("r8d",8),
    R9("r9d",9),
    R10("r10d",10),
    R11("r11d",2),
    R12("r12d",3),
    R13("r13d",11),
    R14("r14d",12),
    R15("r15d",13);

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