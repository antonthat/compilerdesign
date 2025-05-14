.global main 
.global _main
.text
main:
call _main 
movq %rax, %rdi
movq $0x3C, %rax 
syscall
_main:
mov $0x37,%eax
mov $0x39,%ecx
mov %eax,%ebx
imul %ecx,%ebx
mov $0x3f,%esi
mov $0x31,8(%rsp)
mov %esi,%eax
cdq
mov 8(%rsp),%r11d
idiv %r11d
mov %edx,%eax
sub %eax,%ebx
mov $0x3d,%edi
mov $0x60,%r8d
mov %edi,%eax
cdq
idiv %r8d
mov $0x33,%r14d
cdq
idiv %r14d
add %eax,%ebx
mov $0x41,%r10d
add %r10d,%ebx
mov $0xa,16(%rsp)
mov $0x1f,24(%rsp)
mov 16(%rsp),%eax
cdq
mov 24(%rsp),%r11d
idiv %r11d
mov %edx,%r9d
mov %ebx,%eax
add %r9d,%eax
sub 24(%rsp),%eax
mov $0x32,32(%rsp)
sub 32(%rsp),%eax
mov $0x1e,40(%rsp)
sub 40(%rsp),%eax
mov %eax,%ebx
sub 8(%rsp),%ebx
mov $0x4c,48(%rsp)
mov $0xc,56(%rsp)
mov 48(%rsp),%r9d
imul 56(%rsp),%r9d
mov $0x2c,64(%rsp)
mov %r9d,%eax
imul 64(%rsp),%eax
mov $0x15,72(%rsp)
cdq
mov 72(%rsp),%r11d
idiv %r11d
mov $0x59,80(%rsp)
imul 80(%rsp),%eax
sub %eax,%ebx
mov $0x45,88(%rsp)
mov $0x14,96(%rsp)
mov 88(%rsp),%eax
cdq
mov 96(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov $0x62,104(%rsp)
cdq
mov 104(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov %ebx,%r13d
sub %eax,%r13d
mov $0x53,112(%rsp)
mov $0x29,120(%rsp)
mov 112(%rsp),%eax
cdq
mov 120(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov $0xf,128(%rsp)
cdq
mov 128(%rsp),%r11d
idiv %r11d
mov %eax,%ebx
mov %r13d,%eax
add %ebx,%eax
mov $0x58,136(%rsp)
mov %eax,144(%rsp)
mov 136(%rsp),%r11d
add %r11d,144(%rsp)
mov %ecx,%eax
imul %esi,%eax
cdq
mov 8(%rsp),%r11d
idiv %r11d
mov 144(%rsp),%ebx
add %eax,%ebx
mov %edi,%eax
imul %r8d,%eax
cdq
idiv %r14d
cdq
idiv %r10d
mov %edx,%eax
sub %eax,%ebx
mov 16(%rsp),%eax
cdq
mov 24(%rsp),%r11d
idiv %r11d
sub %eax,%ebx
mov 24(%rsp),%eax
cdq
mov 32(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 40(%rsp),%r11d
idiv %r11d
imul 8(%rsp),%eax
add %eax,%ebx
mov %r9d,%eax
cdq
mov 64(%rsp),%r11d
idiv %r11d
cdq
mov 72(%rsp),%r11d
idiv %r11d
add %eax,%ebx
add 80(%rsp),%ebx
mov 88(%rsp),%eax
cdq
mov 96(%rsp),%r11d
idiv %r11d
imul 104(%rsp),%eax
cdq
mov 112(%rsp),%r11d
idiv %r11d
sub %eax,%ebx
add 120(%rsp),%ebx
mov 128(%rsp),%eax
cdq
mov 136(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov %ebx,152(%rsp)
sub %eax,152(%rsp)
mov 152(%rsp),%eax
cdq
idiv %esi
imul 8(%rsp),%eax
cdq
idiv %edi
cdq
idiv %r8d
cdq
idiv %r14d
mov %edx,%eax
mov 144(%rsp),%ecx
sub %eax,%ecx
mov %r10d,%esi
imul 16(%rsp),%esi
mov %esi,%eax
cdq
mov 24(%rsp),%r11d
idiv %r11d
mov %edx,%eax
imul 24(%rsp),%eax
cdq
mov 32(%rsp),%r11d
idiv %r11d
mov %eax,%ebx
mov %ecx,%eax
sub %ebx,%eax
mov %eax,%ecx
sub 40(%rsp),%ecx
mov 8(%rsp),%eax
cdq
mov 48(%rsp),%r11d
idiv %r11d
cdq
mov 56(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 64(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 72(%rsp),%r11d
idiv %r11d
mov %edx,%eax
imul 80(%rsp),%eax
imul 88(%rsp),%eax
mov %eax,%ebx
imul 96(%rsp),%ebx
mov %ecx,%eax
add %ebx,%eax
sub 104(%rsp),%eax
mov %eax,%ebx
sub 112(%rsp),%ebx
mov 120(%rsp),%eax
cdq
mov 128(%rsp),%r11d
idiv %r11d
mov %edx,%ecx
mov %ebx,%eax
add %ecx,%eax
mov %eax,160(%rsp)
mov 136(%rsp),%r11d
sub %r11d,160(%rsp)
mov 144(%rsp),%r11d
mov %r11d,168(%rsp)
mov 152(%rsp),%r11d
mov 168(%rsp),%r12d
imul %r11d,%r12d
mov %r12d,168(%rsp)
mov 168(%rsp),%r11d
mov %r11d,176(%rsp)
mov 160(%rsp),%r11d
mov 176(%rsp),%r12d
imul %r11d,%r12d
mov %r12d,176(%rsp)
mov 144(%rsp),%eax
cdq
mov 152(%rsp),%r11d
idiv %r11d
mov %eax,%ebx
mov 160(%rsp),%eax
cdq
mov 8(%rsp),%r11d
idiv %r11d
add %eax,%ebx
mov %ebx,%eax
sub %edi,%eax
mov %eax,%ebx
add %r8d,%ebx
mov %r14d,%eax
imul %r10d,%eax
cdq
mov 16(%rsp),%r11d
idiv %r11d
mov %edx,%eax
sub %eax,%ebx
mov 24(%rsp),%eax
cdq
mov 24(%rsp),%r11d
idiv %r11d
mov %edx,%eax
sub %eax,%ebx
mov 32(%rsp),%eax
imul 40(%rsp),%eax
add %eax,%ebx
mov 8(%rsp),%eax
cdq
mov 48(%rsp),%r11d
idiv %r11d
add %eax,%ebx
mov 56(%rsp),%eax
cdq
mov 64(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 72(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 80(%rsp),%r11d
idiv %r11d
mov %edx,%ecx
mov %ebx,%eax
sub %ecx,%eax
mov 88(%rsp),%ebx
imul 96(%rsp),%ebx
add %ebx,%eax
mov %eax,%ebx
sub 104(%rsp),%ebx
mov 112(%rsp),%r11d
mov %r11d,184(%rsp)
mov 120(%rsp),%r11d
mov 184(%rsp),%r12d
imul %r11d,%r12d
mov %r12d,184(%rsp)
mov 184(%rsp),%r11d
mov %r11d,192(%rsp)
mov 128(%rsp),%r11d
mov 192(%rsp),%r12d
imul %r11d,%r12d
mov %r12d,192(%rsp)
mov 192(%rsp),%eax
cdq
mov 136(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov %ebx,200(%rsp)
sub %eax,200(%rsp)
mov 152(%rsp),%eax
cdq
mov 160(%rsp),%r11d
idiv %r11d
imul 200(%rsp),%eax
cdq
idiv %edi
mov %edx,%eax
cdq
idiv %r8d
mov %edx,%ebx
mov 144(%rsp),%eax
add %ebx,%eax
sub %r14d,%eax
mov %eax,%ecx
add %esi,%ecx
mov 24(%rsp),%eax
cdq
mov 24(%rsp),%r11d
idiv %r11d
mov %eax,%ebx
mov %ecx,%eax
sub %ebx,%eax
add 32(%rsp),%eax
mov %eax,%ebx
add 40(%rsp),%ebx
mov 8(%rsp),%eax
cdq
mov 48(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov %ebx,%ecx
add %eax,%ecx
mov 56(%rsp),%eax
cdq
mov 64(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 72(%rsp),%r11d
idiv %r11d
imul 80(%rsp),%eax
cdq
mov 88(%rsp),%r11d
idiv %r11d
cdq
mov 96(%rsp),%r11d
idiv %r11d
mov %eax,%ebx
imul 104(%rsp),%ebx
mov %ecx,%eax
sub %ebx,%eax
mov %eax,%ebx
sub 112(%rsp),%ebx
mov 120(%rsp),%r11d
mov %r11d,208(%rsp)
mov 128(%rsp),%r11d
mov 208(%rsp),%r12d
imul %r11d,%r12d
mov %r12d,208(%rsp)
mov 208(%rsp),%eax
cdq
mov 136(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov %ebx,216(%rsp)
sub %eax,216(%rsp)
mov 200(%rsp),%r11d
mov %r11d,224(%rsp)
mov 216(%rsp),%r11d
mov 224(%rsp),%r12d
imul %r11d,%r12d
mov %r12d,224(%rsp)
mov 144(%rsp),%ebx
sub 152(%rsp),%ebx
mov 160(%rsp),%r11d
mov %r11d,232(%rsp)
mov 200(%rsp),%r11d
mov 232(%rsp),%r12d
imul %r11d,%r12d
mov %r12d,232(%rsp)
mov 232(%rsp),%eax
cdq
mov 216(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
idiv %r8d
cdq
idiv %r14d
cdq
idiv %r10d
cdq
mov 16(%rsp),%r11d
idiv %r11d
cdq
mov 24(%rsp),%r11d
idiv %r11d
cdq
mov 24(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 32(%rsp),%r11d
idiv %r11d
mov %edx,%eax
sub %eax,%ebx
mov 40(%rsp),%eax
cdq
mov 8(%rsp),%r11d
idiv %r11d
mov %ebx,%ecx
add %eax,%ecx
mov 48(%rsp),%eax
cdq
mov 56(%rsp),%r11d
idiv %r11d
mov %edx,%eax
imul 64(%rsp),%eax
imul 72(%rsp),%eax
imul 80(%rsp),%eax
mov %eax,%ebx
imul 88(%rsp),%ebx
mov %ecx,%eax
sub %ebx,%eax
add 96(%rsp),%eax
add 104(%rsp),%eax
mov %eax,%ebx
sub 112(%rsp),%ebx
mov 120(%rsp),%eax
cdq
mov 128(%rsp),%r11d
idiv %r11d
cdq
mov 136(%rsp),%r11d
idiv %r11d
mov %ebx,240(%rsp)
sub %eax,240(%rsp)
mov 80(%rsp),%r11d
mov %r11d,248(%rsp)
mov 88(%rsp),%r11d
mov 248(%rsp),%r12d
imul %r11d,%r12d
mov %r12d,248(%rsp)
mov 248(%rsp),%r11d
mov %r11d,256(%rsp)
mov 96(%rsp),%r11d
mov 256(%rsp),%r12d
imul %r11d,%r12d
mov %r12d,256(%rsp)
mov 168(%rsp),%r11d
mov %r11d,264(%rsp)
mov 160(%rsp),%r11d
sub %r11d,264(%rsp)
mov 144(%rsp),%r11d
mov %r11d,272(%rsp)
mov 152(%rsp),%r11d
add %r11d,272(%rsp)
mov 160(%rsp),%eax
cdq
mov 200(%rsp),%r11d
idiv %r11d
cdq
mov 216(%rsp),%r11d
idiv %r11d
mov 272(%rsp),%ecx
add %eax,%ecx
mov 240(%rsp),%eax
imul %r14d,%eax
cdq
idiv %r10d
mov %edx,%eax
cdq
mov 16(%rsp),%r11d
idiv %r11d
mov %edx,%ebx
mov %ecx,%eax
add %ebx,%eax
sub 24(%rsp),%eax
add 24(%rsp),%eax
add 32(%rsp),%eax
add 40(%rsp),%eax
mov %eax,%ebx
sub 8(%rsp),%ebx
mov 48(%rsp),%eax
cdq
mov 56(%rsp),%r11d
idiv %r11d
mov %edx,%eax
sub %eax,%ebx
mov 64(%rsp),%eax
imul 72(%rsp),%eax
cdq
mov 80(%rsp),%r11d
idiv %r11d
cdq
mov 88(%rsp),%r11d
idiv %r11d
mov %edx,%eax
add %eax,%ebx
mov 96(%rsp),%edi
imul 104(%rsp),%edi
mov %edi,%eax
cdq
mov 112(%rsp),%r11d
idiv %r11d
sub %eax,%ebx
add 120(%rsp),%ebx
mov 128(%rsp),%eax
imul 136(%rsp),%eax
mov %ebx,280(%rsp)
sub %eax,280(%rsp)
mov 152(%rsp),%eax
cdq
mov 160(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov 144(%rsp),%ebx
add %eax,%ebx
sub 200(%rsp),%ebx
mov 216(%rsp),%ecx
imul 240(%rsp),%ecx
mov %ecx,%eax
cdq
mov 280(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
idiv %r10d
cdq
mov 16(%rsp),%r11d
idiv %r11d
mov %eax,%esi
mov %ebx,%eax
add %esi,%eax
add 24(%rsp),%eax
sub 24(%rsp),%eax
mov %eax,%ebx
sub 32(%rsp),%ebx
mov 40(%rsp),%eax
cdq
mov 8(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 48(%rsp),%r11d
idiv %r11d
mov %edx,%eax
add %eax,%ebx
mov 56(%rsp),%eax
cdq
mov 64(%rsp),%r11d
idiv %r11d
mov %edx,%eax
imul 72(%rsp),%eax
cdq
mov 80(%rsp),%r11d
idiv %r11d
cdq
mov 88(%rsp),%r11d
idiv %r11d
mov %ebx,%esi
add %eax,%esi
mov %edi,%eax
imul 112(%rsp),%eax
cdq
mov 120(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 128(%rsp),%r11d
idiv %r11d
mov %edx,%ebx
mov %esi,%eax
sub %ebx,%eax
mov %eax,288(%rsp)
mov 136(%rsp),%r11d
add %r11d,288(%rsp)
mov 160(%rsp),%eax
cdq
mov 200(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 216(%rsp),%r11d
idiv %r11d
mov %eax,%edi
mov 40(%rsp),%eax
cdq
mov 8(%rsp),%r11d
idiv %r11d
mov %eax,%r8d
mov 56(%rsp),%eax
cdq
mov 64(%rsp),%r11d
idiv %r11d
cdq
mov 72(%rsp),%r11d
idiv %r11d
mov %edx,%r10d
mov 96(%rsp),%eax
cdq
mov 104(%rsp),%r11d
idiv %r11d
mov %edx,%r13d
mov 120(%rsp),%eax
cdq
mov 128(%rsp),%r11d
idiv %r11d
cdq
mov 136(%rsp),%r11d
idiv %r11d
mov %eax,%r14d
mov 144(%rsp),%eax
cdq
mov 152(%rsp),%r11d
idiv %r11d
cdq
mov 160(%rsp),%r11d
idiv %r11d
mov %eax,%r15d
mov 216(%rsp),%eax
cdq
mov 240(%rsp),%r11d
idiv %r11d
mov %eax,296(%rsp)
mov 280(%rsp),%eax
cdq
mov 288(%rsp),%r11d
idiv %r11d
mov %eax,304(%rsp)
mov 48(%rsp),%eax
cdq
mov 56(%rsp),%r11d
idiv %r11d
mov %edx,%ebx
mov 64(%rsp),%eax
cdq
mov 72(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 80(%rsp),%r11d
idiv %r11d
cdq
mov 88(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 96(%rsp),%r11d
idiv %r11d
cdq
mov 104(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 112(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 120(%rsp),%r11d
idiv %r11d
mov %edx,%eax
imul 128(%rsp),%eax
cdq
mov 136(%rsp),%r11d
idiv %r11d
mov %edx,%r9d
mov 152(%rsp),%eax
cdq
mov 160(%rsp),%r11d
idiv %r11d
mov %edx,%eax
imul 200(%rsp),%eax
cdq
mov 216(%rsp),%r11d
idiv %r11d
mov %edx,%eax
imul 240(%rsp),%eax
cdq
mov 280(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 288(%rsp),%r11d
idiv %r11d
mov %edx,%esi
mov 168(%rsp),%eax
add %edi,%eax
mov 240(%rsp),%edi
imul 280(%rsp),%edi
imul 288(%rsp),%edi
sub %edi,%eax
add 16(%rsp),%eax
add 24(%rsp),%eax
add 24(%rsp),%eax
sub 32(%rsp),%eax
sub %r8d,%eax
sub 48(%rsp),%eax
sub %r10d,%eax
sub 248(%rsp),%eax
sub %r13d,%eax
sub 112(%rsp),%eax
mov %eax,312(%rsp)
sub %r14d,312(%rsp)
mov %esi,%eax
cdq
mov 312(%rsp),%r11d
idiv %r11d
mov %eax,%r8d
mov %r15d,%eax
add 200(%rsp),%eax
add 296(%rsp),%eax
sub 304(%rsp),%eax
mov %eax,%edi
add 312(%rsp),%edi
mov 24(%rsp),%eax
imul 24(%rsp),%eax
mov %eax,%esi
imul 32(%rsp),%esi
mov %edi,%eax
sub %esi,%eax
sub 40(%rsp),%eax
add 8(%rsp),%eax
sub %ebx,%eax
mov %eax,320(%rsp)
sub %r9d,320(%rsp)
mov %r8d,%eax
imul 320(%rsp),%eax
mov %eax,%ebx
imul 24(%rsp),%ebx
mov 144(%rsp),%eax
sub %ebx,%eax
mov %eax,%ebx
add 32(%rsp),%ebx
mov 40(%rsp),%eax
cdq
mov 8(%rsp),%r11d
idiv %r11d
cdq
mov 48(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov %ebx,%esi
sub %eax,%esi
mov 56(%rsp),%eax
cdq
mov 64(%rsp),%r11d
idiv %r11d
mov %eax,%ebx
mov %esi,%eax
add %ebx,%eax
mov 72(%rsp),%r15d
imul 80(%rsp),%r15d
mov %eax,%esi
add %r15d,%esi
mov 88(%rsp),%eax
cdq
mov 96(%rsp),%r11d
idiv %r11d
mov %edx,%eax
imul 104(%rsp),%eax
mov %eax,%ebx
imul 112(%rsp),%ebx
mov %esi,%eax
sub %ebx,%eax
mov %eax,%ebx
sub 120(%rsp),%ebx
mov 128(%rsp),%eax
cdq
mov 136(%rsp),%r11d
idiv %r11d
mov %ebx,328(%rsp)
sub %eax,328(%rsp)
mov 160(%rsp),%eax
cdq
mov 200(%rsp),%r11d
idiv %r11d
mov 272(%rsp),%ebx
add %eax,%ebx
mov %ecx,%eax
imul 280(%rsp),%eax
add %eax,%ebx
add 288(%rsp),%ebx
mov 312(%rsp),%eax
imul 320(%rsp),%eax
add %eax,%ebx
mov 328(%rsp),%eax
cdq
mov 32(%rsp),%r11d
idiv %r11d
imul 40(%rsp),%eax
add %eax,%ebx
mov 8(%rsp),%eax
cdq
mov 48(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov %eax,%esi
imul 56(%rsp),%esi
mov %ebx,%eax
sub %esi,%eax
mov %eax,%ebx
sub 64(%rsp),%ebx
mov %r15d,%eax
cdq
mov 88(%rsp),%r11d
idiv %r11d
mov %ebx,%esi
sub %eax,%esi
mov 96(%rsp),%eax
cdq
mov 104(%rsp),%r11d
idiv %r11d
mov %eax,%ebx
mov %esi,%eax
add %ebx,%eax
mov %eax,%ebx
add 112(%rsp),%ebx
mov 120(%rsp),%eax
cdq
mov 128(%rsp),%r11d
idiv %r11d
cdq
mov 136(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov %ebx,336(%rsp)
add %eax,336(%rsp)
mov 328(%rsp),%r11d
mov %r11d,344(%rsp)
mov 336(%rsp),%r11d
mov 344(%rsp),%r12d
imul %r11d,%r12d
mov %r12d,344(%rsp)
mov 144(%rsp),%eax
cdq
mov 152(%rsp),%r11d
idiv %r11d
cdq
mov 160(%rsp),%r11d
idiv %r11d
mov %eax,%esi
mov 200(%rsp),%eax
cdq
mov 216(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 240(%rsp),%r11d
idiv %r11d
mov %edx,%ebx
mov %esi,%eax
sub %ebx,%eax
mov 280(%rsp),%ebx
imul 288(%rsp),%ebx
add %ebx,%eax
mov %eax,%ebx
add 312(%rsp),%ebx
mov 320(%rsp),%eax
cdq
mov 328(%rsp),%r11d
idiv %r11d
add %eax,%ebx
mov 336(%rsp),%eax
cdq
mov 40(%rsp),%r11d
idiv %r11d
cdq
mov 8(%rsp),%r11d
idiv %r11d
sub %eax,%ebx
mov 48(%rsp),%eax
cdq
mov 56(%rsp),%r11d
idiv %r11d
mov %ebx,%esi
sub %eax,%esi
mov 64(%rsp),%eax
cdq
mov 72(%rsp),%r11d
idiv %r11d
mov %edx,%ebx
mov %esi,%eax
add %ebx,%eax
sub 80(%rsp),%eax
mov %eax,%ebx
add 88(%rsp),%ebx
mov 96(%rsp),%eax
cdq
mov 104(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov %ebx,%esi
add %eax,%esi
mov 112(%rsp),%eax
cdq
mov 120(%rsp),%r11d
idiv %r11d
mov %edx,%ebx
mov %esi,%eax
sub %ebx,%eax
sub 128(%rsp),%eax
mov %eax,352(%rsp)
mov 136(%rsp),%r11d
add %r11d,352(%rsp)
mov 8(%rsp),%ebx
imul 48(%rsp),%ebx
mov 144(%rsp),%eax
cdq
mov 152(%rsp),%r11d
idiv %r11d
mov %eax,%r9d
mov 288(%rsp),%eax
cdq
mov 312(%rsp),%r11d
idiv %r11d
mov %edx,%r8d
mov %ebx,%eax
cdq
mov 56(%rsp),%r11d
idiv %r11d
cdq
mov 64(%rsp),%r11d
idiv %r11d
mov %edx,%ebx
mov 80(%rsp),%eax
cdq
mov 88(%rsp),%r11d
idiv %r11d
cdq
mov 96(%rsp),%r11d
idiv %r11d
cdq
mov 104(%rsp),%r11d
idiv %r11d
mov %edx,%edi
mov 152(%rsp),%eax
cdq
mov 160(%rsp),%r11d
idiv %r11d
cdq
mov 200(%rsp),%r11d
idiv %r11d
mov %eax,%r13d
mov 344(%rsp),%eax
cdq
mov 352(%rsp),%r11d
idiv %r11d
mov %edx,%esi
mov %r9d,%eax
imul 160(%rsp),%eax
sub 200(%rsp),%eax
add 216(%rsp),%eax
add 240(%rsp),%eax
add 280(%rsp),%eax
sub %r8d,%eax
mov 320(%rsp),%r14d
imul 328(%rsp),%r14d
sub %r14d,%eax
sub 336(%rsp),%eax
sub 352(%rsp),%eax
sub %ebx,%eax
sub 72(%rsp),%eax
mov %eax,%ebx
add %edi,%ebx
mov 192(%rsp),%eax
imul 136(%rsp),%eax
mov %ebx,360(%rsp)
add %eax,360(%rsp)
mov %esi,%eax
cdq
mov 360(%rsp),%r11d
idiv %r11d
mov %edx,%r8d
mov 48(%rsp),%eax
cdq
mov 56(%rsp),%r11d
idiv %r11d
cdq
mov 64(%rsp),%r11d
idiv %r11d
mov %eax,%edi
mov 72(%rsp),%eax
cdq
mov 80(%rsp),%r11d
idiv %r11d
mov %edx,%r9d
mov 104(%rsp),%eax
cdq
mov 112(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 120(%rsp),%r11d
idiv %r11d
cdq
mov 128(%rsp),%r11d
idiv %r11d
mov %eax,%r10d
mov 200(%rsp),%eax
cdq
mov 216(%rsp),%r11d
idiv %r11d
imul 240(%rsp),%eax
mov 264(%rsp),%ebx
add %eax,%ebx
mov 280(%rsp),%eax
cdq
mov 288(%rsp),%r11d
idiv %r11d
cdq
mov 312(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 320(%rsp),%r11d
idiv %r11d
mov %edx,%eax
add %eax,%ebx
mov 344(%rsp),%eax
imul 352(%rsp),%eax
mov %ebx,%esi
sub %eax,%esi
mov 144(%rsp),%eax
sub %r13d,%eax
sub %ecx,%eax
mov %eax,%ecx
sub 280(%rsp),%ecx
mov 288(%rsp),%eax
imul 312(%rsp),%eax
mov %eax,%ebx
imul 320(%rsp),%ebx
mov %ecx,%eax
add %ebx,%eax
add %r8d,%eax
add %edi,%eax
sub %r9d,%eax
sub 88(%rsp),%eax
sub 96(%rsp),%eax
add %r10d,%eax
mov %eax,368(%rsp)
mov 136(%rsp),%r11d
add %r11d,368(%rsp)
mov 360(%rsp),%eax
cdq
mov 368(%rsp),%r11d
idiv %r11d
mov %edx,%ebx
mov %esi,%eax
sub %ebx,%eax
sub 56(%rsp),%eax
mov %eax,%ebx
sub 64(%rsp),%ebx
mov %r15d,%eax
imul 88(%rsp),%eax
imul 96(%rsp),%eax
imul 104(%rsp),%eax
cdq
mov 112(%rsp),%r11d
idiv %r11d
imul 120(%rsp),%eax
imul 128(%rsp),%eax
sub %eax,%ebx
mov %ebx,376(%rsp)
mov 136(%rsp),%r11d
add %r11d,376(%rsp)
mov 232(%rsp),%ebx
imul 216(%rsp),%ebx
mov 272(%rsp),%eax
add %ebx,%eax
mov %eax,%ecx
add 240(%rsp),%ecx
mov 280(%rsp),%eax
cdq
mov 288(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov %eax,%ebx
imul 312(%rsp),%ebx
mov %ecx,%eax
sub %ebx,%eax
mov %eax,%ebx
sub %r14d,%ebx
mov 336(%rsp),%eax
cdq
mov 352(%rsp),%r11d
idiv %r11d
add %eax,%ebx
mov %ebx,%eax
add 360(%rsp),%eax
sub 368(%rsp),%eax
mov %eax,%ebx
sub 376(%rsp),%ebx
mov 64(%rsp),%eax
cdq
mov 72(%rsp),%r11d
idiv %r11d
cdq
mov 80(%rsp),%r11d
idiv %r11d
mov %edx,%eax
imul 88(%rsp),%eax
cdq
mov 96(%rsp),%r11d
idiv %r11d
add %eax,%ebx
mov 104(%rsp),%r13d
imul 112(%rsp),%r13d
mov %r13d,%eax
cdq
mov 120(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 128(%rsp),%r11d
idiv %r11d
cdq
mov 136(%rsp),%r11d
idiv %r11d
mov %ebx,384(%rsp)
add %eax,384(%rsp)
mov 240(%rsp),%eax
cdq
mov 280(%rsp),%r11d
idiv %r11d
imul 288(%rsp),%eax
cdq
mov 312(%rsp),%r11d
idiv %r11d
cdq
mov 320(%rsp),%r11d
idiv %r11d
mov %edx,%eax
imul 328(%rsp),%eax
cdq
mov 336(%rsp),%r11d
idiv %r11d
mov %edx,%esi
mov 376(%rsp),%eax
cdq
mov 384(%rsp),%r11d
idiv %r11d
mov %edx,%r8d
mov 256(%rsp),%eax
cdq
mov 104(%rsp),%r11d
idiv %r11d
mov %eax,%r9d
mov 144(%rsp),%eax
cdq
mov 152(%rsp),%r11d
idiv %r11d
imul 160(%rsp),%eax
imul 200(%rsp),%eax
cdq
mov 216(%rsp),%r11d
idiv %r11d
mov %eax,%ecx
add 240(%rsp),%ecx
mov 280(%rsp),%eax
cdq
mov 288(%rsp),%r11d
idiv %r11d
cdq
mov 312(%rsp),%r11d
idiv %r11d
mov %edx,%ebx
mov %ecx,%eax
add %ebx,%eax
mov %eax,%ecx
add 320(%rsp),%ecx
mov 328(%rsp),%eax
cdq
mov 336(%rsp),%r11d
idiv %r11d
mov %eax,%ebx
mov %ecx,%eax
sub %ebx,%eax
add 352(%rsp),%eax
mov 360(%rsp),%edi
imul 368(%rsp),%edi
add %edi,%eax
mov %eax,%ecx
sub 376(%rsp),%ecx
mov 176(%rsp),%eax
add 224(%rsp),%eax
add %esi,%eax
mov 352(%rsp),%ebx
imul 360(%rsp),%ebx
sub %ebx,%eax
add 368(%rsp),%eax
add %r8d,%eax
sub 72(%rsp),%eax
sub %r9d,%eax
sub 184(%rsp),%eax
sub 128(%rsp),%eax
mov %eax,392(%rsp)
mov 136(%rsp),%r11d
sub %r11d,392(%rsp)
mov 384(%rsp),%eax
cdq
mov 392(%rsp),%r11d
idiv %r11d
mov %edx,%eax
imul 80(%rsp),%eax
cdq
mov 88(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 96(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 104(%rsp),%r11d
idiv %r11d
mov %edx,%ebx
mov %ecx,%eax
add %ebx,%eax
mov %eax,%ebx
add 112(%rsp),%ebx
mov 120(%rsp),%eax
cdq
mov 128(%rsp),%r11d
idiv %r11d
cdq
mov 136(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov %ebx,400(%rsp)
sub %eax,400(%rsp)
mov 144(%rsp),%eax
cdq
mov 152(%rsp),%r11d
idiv %r11d
cdq
mov 160(%rsp),%r11d
idiv %r11d
mov %edx,%ebx
mov 216(%rsp),%eax
cdq
mov 240(%rsp),%r11d
idiv %r11d
mov %eax,%ecx
mov 320(%rsp),%eax
cdq
mov 328(%rsp),%r11d
idiv %r11d
mov %edx,%esi
mov %edi,%eax
cdq
mov 376(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 384(%rsp),%r11d
idiv %r11d
mov %eax,%r9d
mov 400(%rsp),%eax
cdq
mov 88(%rsp),%r11d
idiv %r11d
mov %edx,%r10d
mov 224(%rsp),%eax
cdq
mov 240(%rsp),%r11d
idiv %r11d
cdq
mov 280(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov 176(%rsp),%edi
sub %eax,%edi
mov 288(%rsp),%eax
cdq
mov 312(%rsp),%r11d
idiv %r11d
mov %eax,%r8d
mov %edi,%eax
add %r8d,%eax
mov %eax,%edi
sub 320(%rsp),%edi
mov 344(%rsp),%eax
cdq
mov 352(%rsp),%r11d
idiv %r11d
cdq
mov 360(%rsp),%r11d
idiv %r11d
cdq
mov 368(%rsp),%r11d
idiv %r11d
mov %edx,%eax
imul 376(%rsp),%eax
cdq
mov 384(%rsp),%r11d
idiv %r11d
cdq
mov 392(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 400(%rsp),%r11d
idiv %r11d
mov %edi,%r8d
add %eax,%r8d
mov %ebx,%edi
add 200(%rsp),%edi
mov %ecx,%eax
imul 280(%rsp),%eax
mov %eax,%ebx
imul 288(%rsp),%ebx
mov %edi,%eax
sub %ebx,%eax
sub 312(%rsp),%eax
add %esi,%eax
mov 336(%rsp),%r11d
mov %r11d,408(%rsp)
mov 352(%rsp),%r11d
mov 408(%rsp),%r12d
imul %r11d,%r12d
mov %r12d,408(%rsp)
sub 408(%rsp),%eax
add %r9d,%eax
sub 392(%rsp),%eax
mov %r10d,%ebx
imul 96(%rsp),%ebx
sub %ebx,%eax
add 104(%rsp),%eax
sub 112(%rsp),%eax
sub 120(%rsp),%eax
sub 128(%rsp),%eax
mov %eax,416(%rsp)
mov 136(%rsp),%r11d
add %r11d,416(%rsp)
mov %r8d,%eax
add 416(%rsp),%eax
add 96(%rsp),%eax
sub %r13d,%eax
add 208(%rsp),%eax
mov %eax,424(%rsp)
mov 136(%rsp),%r11d
sub %r11d,424(%rsp)
mov 152(%rsp),%eax
imul 160(%rsp),%eax
imul 200(%rsp),%eax
cdq
mov 216(%rsp),%r11d
idiv %r11d
cdq
mov 240(%rsp),%r11d
idiv %r11d
imul 280(%rsp),%eax
cdq
mov 288(%rsp),%r11d
idiv %r11d
mov %edx,%r8d
mov 312(%rsp),%eax
cdq
mov 320(%rsp),%r11d
idiv %r11d
mov %eax,%r9d
mov 360(%rsp),%eax
cdq
mov 368(%rsp),%r11d
idiv %r11d
mov %eax,%r10d
mov 392(%rsp),%eax
cdq
mov 400(%rsp),%r11d
idiv %r11d
mov %eax,%r15d
mov 424(%rsp),%eax
cdq
mov 104(%rsp),%r11d
idiv %r11d
mov %eax,%r14d
mov 112(%rsp),%eax
cdq
mov 120(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 128(%rsp),%r11d
idiv %r11d
mov %eax,%r13d
mov 152(%rsp),%eax
cdq
mov 160(%rsp),%r11d
idiv %r11d
imul 200(%rsp),%eax
cdq
mov 216(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov 144(%rsp),%ecx
sub %eax,%ecx
mov 240(%rsp),%eax
cdq
mov 280(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 288(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 312(%rsp),%r11d
idiv %r11d
mov %eax,%ebx
mov %ecx,%eax
add %ebx,%eax
sub 320(%rsp),%eax
add 328(%rsp),%eax
add 336(%rsp),%eax
mov %eax,%ebx
sub 352(%rsp),%ebx
mov 360(%rsp),%eax
cdq
mov 368(%rsp),%r11d
idiv %r11d
imul 376(%rsp),%eax
cdq
mov 384(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 392(%rsp),%r11d
idiv %r11d
cdq
mov 400(%rsp),%r11d
idiv %r11d
mov %edx,%eax
mov %ebx,%edi
add %eax,%edi
mov 416(%rsp),%esi
imul 424(%rsp),%esi
mov 144(%rsp),%eax
sub %r8d,%eax
sub %r9d,%eax
sub 328(%rsp),%eax
mov %eax,%ecx
add 408(%rsp),%ecx
mov %r10d,%eax
imul 376(%rsp),%eax
mov %eax,%ebx
imul 384(%rsp),%ebx
mov %ecx,%eax
add %ebx,%eax
sub %r15d,%eax
add 416(%rsp),%eax
mov %eax,%ebx
add %r14d,%ebx
mov %r13d,%eax
imul 136(%rsp),%eax
mov %ebx,432(%rsp)
sub %eax,432(%rsp)
mov %esi,%eax
imul 432(%rsp),%eax
cdq
mov 112(%rsp),%r11d
idiv %r11d
imul 120(%rsp),%eax
cdq
mov 128(%rsp),%r11d
idiv %r11d
mov %eax,%ebx
mov %edi,%eax
sub %ebx,%eax
mov %eax,440(%rsp)
mov 136(%rsp),%r11d
add %r11d,440(%rsp)
mov 272(%rsp),%ebx
add 160(%rsp),%ebx
mov 200(%rsp),%eax
cdq
mov 216(%rsp),%r11d
idiv %r11d
cdq
mov 240(%rsp),%r11d
idiv %r11d
mov %edx,%eax
imul 280(%rsp),%eax
add %eax,%ebx
mov 288(%rsp),%eax
cdq
mov 312(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 320(%rsp),%r11d
idiv %r11d
cdq
mov 328(%rsp),%r11d
idiv %r11d
add %eax,%ebx
mov 336(%rsp),%eax
cdq
mov 352(%rsp),%r11d
idiv %r11d
mov %edx,%eax
imul 360(%rsp),%eax
imul 368(%rsp),%eax
cdq
mov 376(%rsp),%r11d
idiv %r11d
mov %ebx,%ecx
add %eax,%ecx
mov 384(%rsp),%eax
cdq
mov 392(%rsp),%r11d
idiv %r11d
cdq
mov 400(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 416(%rsp),%r11d
idiv %r11d
mov %eax,%ebx
imul 424(%rsp),%ebx
mov %ecx,%eax
sub %ebx,%eax
mov %eax,%ecx
sub 432(%rsp),%ecx
mov 440(%rsp),%eax
cdq
mov 120(%rsp),%r11d
idiv %r11d
mov %eax,%ebx
mov %ecx,%eax
sub %ebx,%eax
sub 128(%rsp),%eax
mov %eax,%r8d
sub 136(%rsp),%r8d
mov 440(%rsp),%ebx
imul %r8d,%ebx
mov 144(%rsp),%eax
cdq
mov 152(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 160(%rsp),%r11d
idiv %r11d
mov %eax,448(%rsp)
mov 224(%rsp),%eax
cdq
mov 240(%rsp),%r11d
idiv %r11d
mov %edx,456(%rsp)
mov 280(%rsp),%eax
cdq
mov 288(%rsp),%r11d
idiv %r11d
cdq
mov 312(%rsp),%r11d
idiv %r11d
cdq
mov 320(%rsp),%r11d
idiv %r11d
mov %edx,464(%rsp)
mov 352(%rsp),%eax
cdq
mov 360(%rsp),%r11d
idiv %r11d
imul 368(%rsp),%eax
imul 376(%rsp),%eax
cdq
mov 384(%rsp),%r11d
idiv %r11d
imul 392(%rsp),%eax
cdq
mov 400(%rsp),%r11d
idiv %r11d
cdq
mov 416(%rsp),%r11d
idiv %r11d
mov %eax,%ecx
mov 424(%rsp),%eax
cdq
mov 432(%rsp),%r11d
idiv %r11d
mov %edx,%esi
mov %ebx,%eax
cdq
mov 128(%rsp),%r11d
idiv %r11d
mov %eax,%ebx
mov 152(%rsp),%eax
cdq
mov 160(%rsp),%r11d
idiv %r11d
cdq
mov 200(%rsp),%r11d
idiv %r11d
mov %edx,%r15d
mov 240(%rsp),%eax
cdq
mov 280(%rsp),%r11d
idiv %r11d
cdq
mov 288(%rsp),%r11d
idiv %r11d
cdq
mov 312(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 320(%rsp),%r11d
idiv %r11d
mov %edx,%eax
cdq
mov 328(%rsp),%r11d
idiv %r11d
mov %eax,%edi
mov 336(%rsp),%eax
cdq
mov 352(%rsp),%r11d
idiv %r11d
mov %eax,%r9d
mov 360(%rsp),%eax
cdq
mov 368(%rsp),%r11d
idiv %r11d
imul 376(%rsp),%eax
cdq
mov 384(%rsp),%r11d
idiv %r11d
mov %edx,%r10d
mov 392(%rsp),%eax
cdq
mov 400(%rsp),%r11d
idiv %r11d
mov %eax,%r14d
mov 416(%rsp),%eax
cdq
mov 424(%rsp),%r11d
idiv %r11d
mov %edx,%r13d
mov 440(%rsp),%eax
cdq
idiv %r8d
mov %eax,%r8d
mov 448(%rsp),%eax
add 456(%rsp),%eax
sub 464(%rsp),%eax
sub 328(%rsp),%eax
sub 336(%rsp),%eax
sub %ecx,%eax
add %esi,%eax
sub %ebx,%eax
mov %eax,%ebx
sub 136(%rsp),%ebx
mov %r8d,%eax
imul %ebx,%eax
cdq
mov 136(%rsp),%r11d
idiv %r11d
mov %edx,%ecx
mov 144(%rsp),%edx
add %r15d,%edx
sub 216(%rsp),%edx
add %edi,%edx
sub %r9d,%edx
sub %r10d,%edx
sub %r14d,%edx
mov %r13d,%ebx
imul 432(%rsp),%ebx
add %ebx,%edx
sub %ecx,%edx
mov %edx,%eax
ret
