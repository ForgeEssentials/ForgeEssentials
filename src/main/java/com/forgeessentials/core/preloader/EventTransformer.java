package com.forgeessentials.core.preloader;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.entity.EntityAttackedEvent;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class EventTransformer implements IClassTransformer
{

    private boolean obfuscated = true;

    private String m_attackEntityFrom;
    private String srg_attackEntityFrom;
    private String d_attackEntityFrom;
    private String c_DamageSource;

    public EventTransformer()
    {
        try
        {
            obfuscated = Launch.classLoader.getClassBytes("net.minecraft.world.World") == null;
        }
        catch (IOException e)
        {
            /* do nothing */
        }
        if (obfuscated)
        {
            m_attackEntityFrom = "a";
            srg_attackEntityFrom = "func_70097_a";
            d_attackEntityFrom = "(Lro;F)Z";
            c_DamageSource = "ro";
        }
        else
        {
            m_attackEntityFrom = "attackEntityFrom";
            srg_attackEntityFrom = "attackEntityFrom";
            d_attackEntityFrom = "(Lnet/minecraft/util/DamageSource;F)Z";
            c_DamageSource = "net/minecraft/util/DamageSource";
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if (bytes == null)
            return null;
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        boolean transformed = false;

        // Apply transformers
        transformed |= transformAttackEntityFrom(classNode);

        if (!transformed)
            return bytes;
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public boolean transformAttackEntityFrom(ClassNode classNode)
    {
        // Find method to patch
        MethodNode attackEntityFromMethod = null;
        for (final MethodNode methodNode : classNode.methods)
        {
            if (m_attackEntityFrom.equals(methodNode.name) && d_attackEntityFrom.equals(methodNode.desc))
            {
                attackEntityFromMethod = methodNode;
                break;
            }
        }
        if (attackEntityFromMethod == null)
        {
            return false;
        }

        InsnList i = attackEntityFromMethod.instructions;
        AbstractInsnNode insertPoint = i.getFirst().getNext();

        // System.out.println(Thread.currentThread().getStackTrace()[0].getMethodName());
        // GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
        // INVOKESTATIC java/lang/Thread.currentThread ()Ljava/lang/Thread;
        // INVOKEVIRTUAL java/lang/Thread.getStackTrace ()[Ljava/lang/StackTraceElement;
        // ICONST_0
        // AALOAD
        // INVOKEVIRTUAL java/lang/StackTraceElement.getMethodName ()Ljava/lang/String;
        // INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V
        // i.insertBefore(insertPoint, new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        // i.insertBefore(insertPoint, new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false));
        // i.insertBefore(insertPoint, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false));
        // i.insertBefore(insertPoint, new InsnNode(Opcodes.ICONST_2));
        // i.insertBefore(insertPoint, new InsnNode(Opcodes.AALOAD));
        // i.insertBefore(insertPoint, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false));
        // i.insertBefore(insertPoint, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));

        // if (!Thread.currentThread().getStackTrace()[0].getMethodName().equals("attackEntityFrom"))
        // INVOKESTATIC java/lang/Thread.currentThread ()Ljava/lang/Thread;
        // INVOKEVIRTUAL java/lang/Thread.getStackTrace ()[Ljava/lang/StackTraceElement;
        // ICONST_0
        // AALOAD
        // INVOKEVIRTUAL java/lang/StackTraceElement.getMethodName ()Ljava/lang/String;
        // LDC "attackEntityFrom"
        // INVOKEVIRTUAL java/lang/String.equals (Ljava/lang/Object;)Z
        // IFNE L1
        i.insertBefore(insertPoint, new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false));
        i.insertBefore(insertPoint, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false));
        i.insertBefore(insertPoint, new InsnNode(Opcodes.ICONST_2));
        i.insertBefore(insertPoint, new InsnNode(Opcodes.AALOAD));
        i.insertBefore(insertPoint, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false));
        i.insertBefore(insertPoint, new LdcInsnNode(srg_attackEntityFrom));
        i.insertBefore(insertPoint, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false));
        JumpInsnNode jumpNode1;
        i.insertBefore(insertPoint, jumpNode1 = new JumpInsnNode(Opcodes.IFNE, null));

        // EntityAttackedEvent event = new EntityAttackedEvent(this, damageSource, damage);
        // L0
        // NEW net/minecraftforge/fe/event/entity/EntityAttackedEvent
        // DUP
        // ALOAD 0
        // ALOAD 1
        // FLOAD 2
        // INVOKESPECIAL net/minecraftforge/fe/event/entity/EntityAttackedEvent.<init> (Lnet/minecraft/entity/Entity;Lnet/minecraft/util/DamageSource;F)V
        // ASTORE 3
        i.insertBefore(insertPoint, new TypeInsnNode(Opcodes.NEW, "net/minecraftforge/fe/event/entity/EntityAttackedEvent"));
        i.insertBefore(insertPoint, new InsnNode(Opcodes.DUP));
        i.insertBefore(insertPoint, new VarInsnNode(Opcodes.ALOAD, 0));
        i.insertBefore(insertPoint, new VarInsnNode(Opcodes.ALOAD, 1));
        i.insertBefore(insertPoint, new VarInsnNode(Opcodes.FLOAD, 2));
        i.insertBefore(insertPoint, new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraftforge/fe/event/entity/EntityAttackedEvent", "<init>",
                "(Lnet/minecraft/entity/Entity;L" + c_DamageSource + ";F)V", false));
        i.insertBefore(insertPoint, new VarInsnNode(Opcodes.ASTORE, 3));

        // if (MinecraftForge.EVENT_BUS.post(event))
        // L1
        // GETSTATIC net/minecraftforge/common/MinecraftForge.EVENT_BUS : Lcpw/mods/fml/common/eventhandler/EventBus;
        // ALOAD 3
        // INVOKEVIRTUAL cpw/mods/fml/common/eventhandler/EventBus.post (Lcpw/mods/fml/common/eventhandler/Event;)Z
        // IFEQ L2
        i.insertBefore(insertPoint,
                new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
        i.insertBefore(insertPoint, new VarInsnNode(Opcodes.ALOAD, 3));
        i.insertBefore(insertPoint, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post",
                "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
        JumpInsnNode jumpNode2;
        i.insertBefore(insertPoint, jumpNode2 = new JumpInsnNode(Opcodes.IFEQ, null));

        // return event.result;
        // L3
        // ALOAD 3
        // GETFIELD net/minecraftforge/fe/event/entity/EntityAttackedEvent.result : Z
        // IRETURN
        i.insertBefore(insertPoint, new VarInsnNode(Opcodes.ALOAD, 3));
        i.insertBefore(insertPoint, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/fe/event/entity/EntityAttackedEvent", "result", "Z"));
        i.insertBefore(insertPoint, new InsnNode(Opcodes.IRETURN));

        // damage = event.damage;
        // L2
        // FRAME APPEND [net/minecraftforge/fe/event/entity/EntityAttackedEvent]
        // ALOAD 3
        // GETFIELD net/minecraftforge/fe/event/entity/EntityAttackedEvent.damage : F
        // FSTORE 2
        i.insertBefore(insertPoint, jumpNode2.label = new LabelNode(new Label()));
        i.insertBefore(insertPoint, new FrameNode(Opcodes.F_APPEND, 1, new String[] { "net/minecraftforge/fe/event/entity/EntityAttackedEvent" }, 0, null));
        i.insertBefore(insertPoint, new VarInsnNode(Opcodes.ALOAD, 3));
        i.insertBefore(insertPoint, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/fe/event/entity/EntityAttackedEvent", "damage", "F"));
        i.insertBefore(insertPoint, new VarInsnNode(Opcodes.FSTORE, 2));

        // last jump node
        i.insertBefore(insertPoint, jumpNode1.label = new LabelNode(new Label()));

        return true;
    }

    /**
     * Just a dummy to view the bytecode necessary for the patch
     */
    public boolean attackEntityFromEvent(DamageSource damageSource, float damage)
    {
        if (!Thread.currentThread().getStackTrace()[2].getMethodName().equals("attackEntityFrom"))
        {
            EntityAttackedEvent event = new EntityAttackedEvent((Entity) (Object) this, damageSource, damage);
            if (MinecraftForge.EVENT_BUS.post(event))
            {
                return event.result;
            }
            damage = event.damage;
        }
        return false;
    }

}