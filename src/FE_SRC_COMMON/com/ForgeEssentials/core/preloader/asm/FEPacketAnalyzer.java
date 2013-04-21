package com.ForgeEssentials.core.preloader.asm;

import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.relauncher.IClassTransformer;

public class FEPacketAnalyzer implements IClassTransformer
{
    public static boolean                   MemoryConnection_addToSendQueue = false;
    public static boolean                   MemoryConnection_processOrCachePacket = false;
    public static boolean                   TcpConnection_addToSendQueue = false;
    public static boolean                   TcpConnection_readPacket = false;
    
    public static HashMap<String, String>   MCdev = new HashMap<String, String>();
    public static HashMap<String, String>   TCdev = new HashMap<String, String>();
    
    private static final String             ANALYZERCLASS = "com/ForgeEssentials/api/packetInspector/PacketAnalyzerRegistry";
    private static final String             outgoingMethodName = "handleOutgoing";
    private static final String             incomingMethodName = "handleIncoming";
    
    static
    {
        MCdev.put("className", "net.minecraft.network.MemoryConnection");
        MCdev.put("targetMethod1", "addToSendQueue");
        MCdev.put("targetMethod2", "processOrCachePacket");
        MCdev.put("packetName", "net/minecraft/network/packet/Packet");
        
        TCdev.put("className", "net.minecraft.network.TcpConnection");
        TCdev.put("targetMethod1", "addToSendQueue");
        TCdev.put("targetMethod2", "readPacket");
        TCdev.put("packetName", "net/minecraft/network/packet/Packet");
    }
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if (name.equals(MCdev.get("className")))
            return transformMemoryConnection(bytes, MCdev);
        
        if (name.equals(TCdev.get("className")))
            return transformTcpConnection(bytes, TCdev);
        
        return bytes;
    }

    
    private byte[] transformMemoryConnection(byte[] bytes, HashMap<String, String> hm)
    {
        System.out.println("[FE coremod] Patching MemoryConnection...");
        
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();

        while (methods.hasNext())
        {
            MethodNode m = methods.next();
            
            if (hm.get("targetMethod1").equals(m.name))
            {
                System.out.println("[FE coremod] Found addToSendQueue");
                
                int offset = 0;
                while (m.instructions.get(offset).getOpcode() != Opcodes.ALOAD)
                {
                    offset++;
                }
                
                LabelNode lmm1Node = new LabelNode(new Label());
                LabelNode lmm2Node = new LabelNode(new Label());

                InsnList toInject = new InsnList();
                
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
                toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ANALYZERCLASS, outgoingMethodName, "(L" + hm.get("packetName") + ";)L" + hm.get("packetName") + ";"));
                toInject.add(new VarInsnNode(Opcodes.ASTORE, 1));
                toInject.add(lmm1Node);
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
                toInject.add(new JumpInsnNode(Opcodes.IFNONNULL, lmm2Node));
                toInject.add(new InsnNode(Opcodes.RETURN));
                toInject.add(lmm2Node);
                
                m.instructions.insertBefore(m.instructions.get(offset), toInject);
                
                System.out.println("[FE coremod] Patching addToSendQueue Complete!");
            }
            else if (hm.get("targetMethod2").equals(m.name))
            {
                System.out.println("[FE coremod] Found processOrCachePacket");
                
                int offset = 0;
                while (m.instructions.get(offset).getOpcode() != Opcodes.ALOAD)
                {
                    offset++;
                }
                
                LabelNode lmm1Node = new LabelNode(new Label());
                LabelNode lmm2Node = new LabelNode(new Label());
                
                InsnList toInject = new InsnList();
                
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
                toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ANALYZERCLASS, incomingMethodName, "(L" + hm.get("packetName") + ";)L" + hm.get("packetName") + ";"));
                toInject.add(new VarInsnNode(Opcodes.ASTORE, 1));
                toInject.add(lmm1Node);
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
                toInject.add(new JumpInsnNode(Opcodes.IFNONNULL, lmm2Node));
                toInject.add(new InsnNode(Opcodes.RETURN));
                toInject.add(lmm2Node);
                
                m.instructions.insertBefore(m.instructions.get(offset), toInject);
                
                System.out.println("[FE coremod] Patching processOrCachePacket Complete!");
            }
        }
        
        System.out.println("[FE coremod] Patching MemoryConnection done!");
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
    /**
     * Method for transforming TcpConnection
     * @param bytes
     * @param hm
     * @return
     */
    private byte[] transformTcpConnection(byte[] bytes, HashMap<String, String> hm)
    {
        System.out.println("[FE coremod] Patching TcpConnection...");
        
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();

        while (methods.hasNext())
        {
            MethodNode m = methods.next();
            
            if (hm.get("targetMethod1").equals(m.name))
            {
                System.out.println("[FE coremod] Found addToSendQueue");
                
                int offset = 0;
                while (m.instructions.get(offset).getOpcode() != Opcodes.ALOAD)
                {
                    offset++;
                }
                
                LabelNode lmm1Node = new LabelNode(new Label());
                LabelNode lmm2Node = new LabelNode(new Label());

                InsnList toInject = new InsnList();
                
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
                toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ANALYZERCLASS, outgoingMethodName, "(L" + hm.get("packetName") + ";)L" + hm.get("packetName") + ";"));
                toInject.add(new VarInsnNode(Opcodes.ASTORE, 1));
                toInject.add(lmm1Node);
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
                toInject.add(new JumpInsnNode(Opcodes.IFNONNULL, lmm2Node));
                toInject.add(new InsnNode(Opcodes.RETURN));
                toInject.add(lmm2Node);
                
                m.instructions.insertBefore(m.instructions.get(offset), toInject);
                
                TcpConnection_addToSendQueue = true;
                System.out.println("[FE coremod] Patching addToSendQueue Complete!");
            }
            else if (hm.get("targetMethod2").equals(m.name))
            {
                System.out.println("[FE coremod] Found readPacket");
                
                int offset = 0;
                while (m.instructions.get(offset).getOpcode() != Opcodes.IFNULL)
                {
                    offset++;
                }
                
                LabelNode lmm1Node = new LabelNode(new Label());
                LabelNode lmm2Node = new LabelNode(new Label());
                
                InsnList toInject = new InsnList();
                
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 2));
                toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ANALYZERCLASS, incomingMethodName, "(L" + hm.get("packetName") + ";)L" + hm.get("packetName") + ";"));
                toInject.add(new VarInsnNode(Opcodes.ASTORE, 2));
                toInject.add(lmm1Node);
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 2));
                toInject.add(new JumpInsnNode(Opcodes.IFNONNULL, lmm2Node));
                toInject.add(new InsnNode(Opcodes.ICONST_1));
                toInject.add(new InsnNode(Opcodes.IRETURN));
                toInject.add(lmm2Node);
                
                m.instructions.insert(m.instructions.get(offset), toInject);
                
                TcpConnection_readPacket = true;
                System.out.println("[FE coremod] Patching readPacket Complete!");
            }
        }
        
        System.out.println("[FE coremod] Patching TcpConnection done!");
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}