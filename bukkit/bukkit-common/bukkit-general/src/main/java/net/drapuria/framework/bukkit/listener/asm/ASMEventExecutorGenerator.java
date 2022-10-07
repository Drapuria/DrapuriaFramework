/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.listener.asm;

import lombok.NonNull;
import org.bukkit.plugin.EventExecutor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;

public class ASMEventExecutorGenerator {
    @NonNull
    public static byte[] generateEventExecutor(@NonNull Method m, @NonNull String name) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        writer.visit(
            52,
            ACC_PUBLIC, name.replace('.', '/'),
            null,
            Type.getInternalName(Object.class),
            new String[] {Type.getInternalName(EventExecutor.class)}
            );
        // Generate constructor
        GeneratorAdapter methodGenerator = new GeneratorAdapter(
            writer.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null),
             ACC_PUBLIC,
             "<init>", "()V");
        methodGenerator.loadThis();
        methodGenerator.visitMethodInsn(INVOKESPECIAL,
         Type.getInternalName(Object.class),
          "<init>", "()V"); // Invoke the super class (Object) constructor
        methodGenerator.returnValue();
        methodGenerator.endMethod();
        // Generate the execute method
        methodGenerator = new GeneratorAdapter(writer.visitMethod(ACC_PUBLIC, 
        "execute", 
        "(Lorg/bukkit/event/Listener;Lorg/bukkit/event/Event;)V", 
        null, 
        null), ACC_PUBLIC, "execute", "(Lorg/bukkit/event/Listener;Lorg/bukkit/event/Listener;)V");;
        methodGenerator.loadArg(0);
        methodGenerator.checkCast(Type.getType(m.getDeclaringClass()));
        methodGenerator.loadArg(1);
        methodGenerator.checkCast(Type.getType(m.getParameterTypes()[0]));
        methodGenerator.visitMethodInsn(
            m.getDeclaringClass().isInterface() ? INVOKEINTERFACE : INVOKEVIRTUAL,
             Type.getInternalName(m.getDeclaringClass()), m.getName(), Type.getMethodDescriptor(m));
        if (m.getReturnType() != void.class) {
            methodGenerator.pop();
        }
        methodGenerator.returnValue();
        methodGenerator.endMethod();
        writer.visitEnd();
        return writer.toByteArray();
    }

    public static AtomicInteger NEXT_ID = new AtomicInteger(1);
    @NonNull
    public static String generateName() {
        int id = NEXT_ID.getAndIncrement();
        return "net.drapuria.framework.bukkit.listener.asm.generated.GeneratedEventExecutor" + id;
    }
}
