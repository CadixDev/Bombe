package me.jamiemansfield.bombe.asm.analysis;

import me.jamiemansfield.bombe.analysis.InheritanceProvider;
import me.jamiemansfield.bombe.analysis.InheritanceType;
import me.jamiemansfield.bombe.type.signature.FieldSignature;
import me.jamiemansfield.bombe.type.signature.MethodSignature;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class InheritanceClassInfoVisitor extends ClassVisitor {

    private String name;
    private boolean isInterface;
    private String superName;
    private List<String> interfaces = Collections.emptyList();

    private final Map<FieldSignature, InheritanceType> fields = new HashMap<>();
    private final Map<String, InheritanceType> fieldsByName = new HashMap<>();
    private final Map<MethodSignature, InheritanceType> methods = new HashMap<>();

    InheritanceClassInfoVisitor() {
        super(Opcodes.ASM6);
    }

    InheritanceProvider.ClassInfo create() {
        return new InheritanceProvider.ClassInfo.Impl(this.name, this.isInterface, this.superName, this.interfaces,
                this.fields, this.fieldsByName, this.methods);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.name = name;
        this.isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
        this.superName = superName;
        this.interfaces = Arrays.asList(interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        InheritanceType type = InheritanceType.fromModifiers(access);
        this.fields.put(FieldSignature.of(name, descriptor), type);
        this.fieldsByName.put(name, type);
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        this.methods.put(MethodSignature.of(name, descriptor), InheritanceType.fromModifiers(access));
        return null;
    }

}
