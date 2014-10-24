package com.forgeessentials.core.preloader.forge;

import com.forgeessentials.core.preloader.asm.EventInjector;
import com.forgeessentials.core.preloader.asm.EventInjector.ClassPatch;
import com.forgeessentials.core.preloader.asm.EventInjector.MethodMapping;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;


public class FEHooks
{
    // this thing is supposed to be smart.. it should detect forge version/presence of sponge and enable the necessary patches.
    public static void doInit()
    {
        ClassPatch nhps = new ClassPatch("net.minecraft.network.NetHandlerPlayServer");
        nhps.methodMappings.add(new network_NetHandlerPlayServer());
        EventInjector.addClassPatch(nhps);

        initCommandHandlerPatches();
    }

    public static void initCommandHandlerPatches()
    {
        ClassPatch commandHandler = new ClassPatch("net.minecraft.command.CommandHandler");
        commandHandler.methodMappings.add(new MethodMapping("func_71558_b", "getPossibleCommands", "(Lnet/minecraft/command/ICommandSender;Ljava/lang/String;)Ljava/util/List;") {
            @Override
            public void defineMethod(ClassWriter classWriter) {
                MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, getName(), desc, null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/command/CommandHandler", "commandMap", "Ljava/util/Map;");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKESTATIC, "com/forgeessentials/core/preloader/forge/command_CommandHandler", mcpName, "(Ljava/util/Map;Lnet/minecraft/command/ICommandSender;Ljava/lang/String;)Ljava/util/List;", false);
                mv.visitInsn(ARETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "Lnet/minecraft/command/CommandHandler;", null, l0, l1, 0);
                mv.visitLocalVariable("sender", "Lnet/minecraft/command/ICommandSender;", null, l0, l1, 1);
                mv.visitLocalVariable("raw", "Ljava/lang/String;", null, l0, l1, 2);
                mv.visitMaxs(3, 3);
                mv.visitEnd();
            }
        });

        commandHandler.methodMappings.add(new MethodMapping("func_71557_a", "getPossibleCommands", "(Lnet/minecraft/command/ICommandSender;)Ljava/util/List;") {
            @Override
            public void defineMethod(ClassWriter classWriter) {
                MethodVisitor mv = mv = classWriter.visitMethod(ACC_PUBLIC, getName(), "(Lnet/minecraft/command/ICommandSender;)Ljava/util/List;", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/command/CommandHandler", "commandMap", "Ljava/util/Map;");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESTATIC, "com/forgeessentials/core/preloader/forge/command_CommandHandler", mcpName, "(Ljava/util/Map;Lnet/minecraft/command/ICommandSender;)Ljava/util/List;", false);
                mv.visitInsn(ARETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "Lnet/minecraft/command/CommandHandler;", null, l0, l1, 0);
                mv.visitLocalVariable("sender", "Lnet/minecraft/command/ICommandSender;", null, l0, l1, 1);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
            }
        });

        EventInjector.addClassPatch(commandHandler);
    }
}
