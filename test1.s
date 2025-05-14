.global main 
.global _main
.text
main:
call _main 
movq %rax, %rdi
movq $0x3C, %rax 
syscall
_main:
mov $0x7ffffffe,%rax
mov $0x2,%r11
mov $0x3,%rbx
imul %rbx,%r11
mov %r11,%r12
imul %rbx,%r12
mov $0xb,%r11
imul %r11,%r12
mov $0x1f,%r11
imul %r11,%r12
mov $0x97,%r11
imul %r11,%r12
mov $0x14b,%r11
imul %r11,%r12
cqo
idiv %r12
mov %rax,%rdx
mov %rdx,%rax
ret
