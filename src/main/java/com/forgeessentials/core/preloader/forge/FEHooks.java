package com.forgeessentials.core.preloader.forge;

import com.forgeessentials.core.preloader.FEPreLoader;
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
import static org.objectweb.asm.Opcodes.*;

public class FEHooks
{
    // this thing is supposed to be smart.. it should detect forge version/presence of sponge and enable the necessary patches.
    public static void doInit()
    {
        initNHPSPatch(); // disable sign patch if 1459 is pulled, disable custompayload patch if 1403 is pulled
        initCommandHandlerPatches();// disable if 1403 is pulled
        // currently not working
        initEntityPlayerMPPatch(); // disable if 1403 is pulled
    }

    public static void initNHPSPatch()
    {
        ClassPatch nhps = new ClassPatch("net.minecraft.network.NetHandlerPlayServer");
        nhps.methodMappings.add(new MethodMapping("func_147343_a", "processUpdateSign", "(Lnet/minecraft/network/play/client/C12PacketUpdateSign;)V", "Sign editing"){
            @Override
            public void defineMethod(ClassWriter classWriter) {
                MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, getName(), "(Lnet/minecraft/network/play/client/C12PacketUpdateSign;)V", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESTATIC, "com/forgeessentials/core/preloader/forge/network_NetHandlerPlayServer", mcpName, "(Lnet/minecraft/network/NetHandlerPlayServer;Lnet/minecraft/network/play/client/C12PacketUpdateSign;)V", false);
                mv.visitInsn(RETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "Lnet/minecraft/network/NetHandlerPlayServer;", null, l0, l1, 0);
                mv.visitLocalVariable("packet", "Lnet/minecraft/network/play/client/C12PacketUpdateSign;", null, l0, l1, 1);
                mv.visitMaxs(2, 2); // change this
                mv.visitEnd();
            }
        });
        nhps.methodMappings.add(new MethodMapping("func_147349_a", "processVanilla250Packet", "(Lnet/minecraft/network/play/client/C17PacketCustomPayload;)V", "Permissions (NetHandlerPlayServer)"){
            @Override
            public void defineMethod(ClassWriter classWriter) {
                MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, getName(), "(Lnet/minecraft/network/play/client/C17PacketCustomPayload;)V", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESTATIC, "com/forgeessentials/core/preloader/forge/network_NetHandlerPlayServer", mcpName, "(Lnet/minecraft/network/NetHandlerPlayServer;Lnet/minecraft/network/play/client/C17PacketCustomPayload;)V", false);
                mv.visitInsn(RETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "Lnet/minecraft/network/NetHandlerPlayServer;", null, l0, l1, 0);
                mv.visitLocalVariable("packet", "Lnet/minecraft/network/play/client/C17PacketCustomPayload;", null, l0, l1, 1);
                mv.visitMaxs(2, 2); // change this
                mv.visitEnd();
            }
        });
        EventInjector.addClassPatch(nhps);
    }

    public static void initCommandHandlerPatches()
    {
        ClassPatch commandHandler = new ClassPatch("net.minecraft.command.CommandHandler");
        commandHandler.methodMappings.add(new MethodMapping("func_71558_b", "getPossibleCommands", "(Lnet/minecraft/command/ICommandSender;Ljava/lang/String;)Ljava/util/List;", "Permissions patch 1, CommandHandler") {
            @Override
            public void defineMethod(ClassWriter classWriter) {
                MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, getName(), desc, null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/command/CommandHandler", getCommandMapFieldName(), "Ljava/util/Map;");
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

        commandHandler.methodMappings.add(new MethodMapping("func_71557_a", "getPossibleCommands", "(Lnet/minecraft/command/ICommandSender;)Ljava/util/List;",  "Permissions patch 2, CommandHandler") {
            @Override
            public void defineMethod(ClassWriter classWriter) {
                MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, getName(), "(Lnet/minecraft/command/ICommandSender;)Ljava/util/List;", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/command/CommandHandler", getCommandMapFieldName(), "Ljava/util/Map;");
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

    public static void initEntityPlayerMPPatch()
    {
        ClassPatch patch = new ClassPatch("net.minecraft.entity.player.EntityPlayerMP");
        patch.methodMappings.add(new MethodMapping("func_70003_b", "canCommandSenderUseCommand", "(ILjava/lang/String;)Z", "Permissions (EntityPlayerMP)")
        {
            @Override public void defineMethod(ClassWriter classWriter)
            {
                MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, getName(), "(ILjava/lang/String;)Z", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitInsn(ICONST_1);
                mv.visitInsn(IRETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "Lnet/minecraft/entity/player/EntityPlayerMP;", null, l0, l1, 0);
                mv.visitLocalVariable("p_70003_1_", "I", null, l0, l1, 1);
                mv.visitLocalVariable("p_70003_2_", "Ljava/lang/String;", null, l0, l1, 2);
                mv.visitMaxs(1, 3);
                mv.visitEnd();
            }
        });
        EventInjector.addClassPatch(patch);
    }

    public static String getCommandMapFieldName()
    {
        if (FEPreLoader.runtimeDeobfEnabled)
            return "field_71562_a";
        else return "commandMap";
    }
}
