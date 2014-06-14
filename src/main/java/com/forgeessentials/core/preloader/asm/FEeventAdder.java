package com.forgeessentials.core.preloader.asm;

import com.forgeessentials.core.preloader.Data;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class FEeventAdder implements IClassTransformer {
    public static boolean addedPlace = false;

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {

        if (name.equals(Data.ISob.get("className")))
        // ItemStack, Obfuscated
        {
            return transformItemStack(bytes, Data.ISob);
        }

        if (name.equals(Data.ISdev.get("className")))
        // ItemStack, NOT Obfuscated
        {
            return transformItemStack(bytes, Data.ISdev);
        }
        return bytes;
    }

    private byte[] transformItemStack(byte[] bytes, HashMap<String, String> hm)
    {
        msg("[FE coremod] Patching ItemStack...");

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();

        while (methods.hasNext())
        {
            MethodNode m = methods.next();

            if (m.name.equals(hm.get("targetMethodName")) && m.desc
                    .equals("(L" + hm.get("entityPlayerJavaClassName") + ";L" + hm.get("worldJavaClassName") + ";IIIIFFF)Z"))
            {
                msg("[FE coremod] Found target method " + m.name + m.desc + "!");

                int offset = 0;
                while (m.instructions.get(offset).getOpcode() != ALOAD)
                {
                    offset++;
                }

                LabelNode lmm1Node = new LabelNode(new Label());
                LabelNode lmm2Node = new LabelNode(new Label());

                InsnList toInject = new InsnList();

                toInject.add(new VarInsnNode(ALOAD, 0));
                toInject.add(new VarInsnNode(ALOAD, 1));
                toInject.add(new VarInsnNode(ALOAD, 2));
                toInject.add(new VarInsnNode(ILOAD, 3));
                toInject.add(new VarInsnNode(ILOAD, 4));
                toInject.add(new VarInsnNode(ILOAD, 5));
                toInject.add(new VarInsnNode(ILOAD, 6));
                toInject.add(new VarInsnNode(FLOAD, 7));
                toInject.add(new VarInsnNode(FLOAD, 8));
                toInject.add(new VarInsnNode(FLOAD, 9));
                toInject.add(new MethodInsnNode(INVOKESTATIC, "com/forgeessentials/util/events/ForgeEssentialsEventFactory", "onBlockPlace",
                        "(L" + hm.get("itemstackJavaClassName") + ";L" + hm.get("entityPlayerJavaClassName") + ";L" + hm.get("worldJavaClassName")
                                + ";IIIIFFF)Z"));
                toInject.add(new JumpInsnNode(IFNE, lmm2Node));
                toInject.add(new InsnNode(ICONST_0));
                toInject.add(new InsnNode(IRETURN));
                toInject.add(lmm2Node);
                toInject.add(lmm1Node);

                m.instructions.insertBefore(m.instructions.get(offset), toInject);
                addedPlace = true;
                msg("[FE coremod] Patching ItemStack Complete!");
                break;
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        if (!addedPlace)
        {
            msg("##########################################################");
            msg("#####                    WARNING                     #####");
            msg("##        [FE coremod] Patching ItemStack FAILED!       ##");
            msg("##########################################################");
        }
        return writer.toByteArray();
    }

    public static void msg(String msg)
    {
        System.out.println(msg);
    }

}
