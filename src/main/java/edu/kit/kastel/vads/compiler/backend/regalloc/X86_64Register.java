package edu.kit.kastel.vads.compiler.backend.regalloc;


public enum X86_64Register implements Register {
    RAX("eax",0),
    RBX("ebx",2),
    RCX("ecx",3),
    RDX("edx",1),
    RSI("esi",4),
    RDI("edi",5),
    RBP("rbp",-4),
    RSP("rsp",-1), // SPECIAL USING RSP!!!
    R8("r8d",6),
    R9("r9d",7),
    R10("r10d",8),
    R11("r11d",-4),
    R12("r12d",-4),
    R13("r13d",9),
    R14("r14d",10),
    R15("r15d",11),
    CL("cl",-4);

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