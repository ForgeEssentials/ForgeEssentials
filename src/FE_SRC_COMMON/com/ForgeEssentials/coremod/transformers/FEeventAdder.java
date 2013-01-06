package com.ForgeEssentials.coremod.transformers;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.FLOAD;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.IClassTransformer;

public class FEeventAdder implements IClassTransformer 
{
	private HashMap<String, String> iiwmHM;
	private HashMap<String, String> isHM;
	
	public static void msg(String msg)
	{
		System.out.println(msg);
	}
	
	public FEeventAdder()
	{
		if(ObfuscationReflectionHelper.obfuscation)
		{
			iiwmHM = new HashMap();
			
			iiwmHM.put("className", "ir");
			iiwmHM.put("javaClassName", "ir");
			iiwmHM.put("targetMethodName", "d");
			iiwmHM.put("worldFieldName", "a");
			iiwmHM.put("entityPlayerFieldName", "b");
			iiwmHM.put("worldJavaClassName", "yc");
			iiwmHM.put("getBlockMetadataMethodName", "h");
			iiwmHM.put("blockJavaClassName", "amq");
			iiwmHM.put("blocksListFieldName", "p");
			iiwmHM.put("entityPlayerJavaClassName", "qx");
			iiwmHM.put("entityPlayerMPJavaClassName", "iq");
			
			isHM = new HashMap<String, String>();
			
			isHM.put("className", "ur");
			isHM.put("javaClassName", "ur");
			isHM.put("targetMethodName", "a");
			isHM.put("itemstackJavaClassName", "ur");
			isHM.put("entityPlayerJavaClassName", "qx");	
			isHM.put("worldJavaClassName", "yc");	
		}
		else
		{	
			iiwmHM = new HashMap<String, String>();
			
			iiwmHM.put("className", "net.minecraft.item.ItemInWorldManager");
			iiwmHM.put("javaClassName", "net/minecraft/item/ItemInWorldManager");
			iiwmHM.put("targetMethodName", "removeBlock");
			iiwmHM.put("worldFieldName", "theWorld");
			iiwmHM.put("entityPlayerFieldName", "thisPlayerMP");
			iiwmHM.put("worldJavaClassName", "net/minecraft/world/World");
			iiwmHM.put("getBlockMetaiiwmHMMethodName", "getBlockMetaiiwmHM");
			iiwmHM.put("blockJavaClassName", "net/minecraft/block/Block");
			iiwmHM.put("blocksListFieldName", "blocksList");
			iiwmHM.put("entityPlayerJavaClassName", "net/minecraft/entity/player/EntityPlayer");	
			iiwmHM.put("entityPlayerMPJavaClassName", "net/minecraft/entity/player/EntityPlayerMP");
			
			
			isHM = new HashMap<String, String>();
			
			isHM.put("className", "net.minecraft.item.ItemStack");
			isHM.put("javaClassName", "net/minecraft/item/ItemStack");
			isHM.put("targetMethodName", "tryPlaceItemIntoWorld");
			
			isHM.put("itemstackJavaClassName", "net/minecraft/item/ItemStack");
			isHM.put("entityPlayerJavaClassName", "net/minecraft/entity/player/EntityPlayer");	
			isHM.put("worldJavaClassName", "net/minecraft/world/World");	
		}
	}
	
	@Override
	public byte[] transform(String name, byte[] bytes) 
	{
		if(name.equals(iiwmHM.get("className")))
		{
			return transformItemInWorldManager(bytes);
		}
		else if(name.equals(isHM.get("className")))
		{
			return transformItemStack(bytes);
		}
		else
		{
			return bytes;
		}
	}
	
	private byte[] transformItemStack(byte[] bytes)
	{
		msg("[FE coremod] Patching ItemStack...");
        HashMap<String, String> hm = isHM;
        
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        
        Iterator<MethodNode> methods = classNode.methods.iterator();
        
        while (methods.hasNext())
        {
            MethodNode m = methods.next();    
            
            if (m.name.equals(hm.get("targetMethodName")) && m.desc.equals("(L" + hm.get("entityPlayerJavaClassName") + ";L" + hm.get("worldJavaClassName") + ";IIIIFFF)Z"))
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
                toInject.add(new MethodInsnNode(INVOKESTATIC, "com/ForgeEssentials/core/CustomEventFactory", "onBlockPlace", "(L" + (String) hm.get("itemstackJavaClassName") + ";L" + (String) hm.get("entityPlayerJavaClassName") + ";L" + (String) hm.get("worldJavaClassName") + ";IIIIFFF)Z"));
                toInject.add(new JumpInsnNode(IFNE, lmm2Node));
                toInject.add(new InsnNode(ICONST_0));
                toInject.add(new InsnNode(IRETURN));
                toInject.add(lmm2Node);
                toInject.add(lmm1Node);
                
                m.instructions.insertBefore(m.instructions.get(offset), toInject);
                 
                msg("[FE coremod] Patching ItemInWorldManager Complete!");
                break;
            }
        }
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
	}
	
	
    private byte[] transformItemInWorldManager(byte[] bytes)
    {    	
        msg("[FE coremod] Patching ItemInWorldManager...");
        HashMap<String, String> hm = iiwmHM;
        
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        
        Iterator<MethodNode> methods = classNode.methods.iterator();
        while (methods.hasNext())
        {
            MethodNode m = methods.next();
            if (m.name.equals(hm.get("targetMethodName")) && m.desc.equals("(III)Z"))
            {
                int blockIndex = 4;
                int mdIndex = 5;
                
                for (int index = 0; index < m.instructions.size(); index++)
                {
                	
                    if (m.instructions.get(index).getType() == AbstractInsnNode.FIELD_INSN)
                    {
                        FieldInsnNode blocksListNode = (FieldInsnNode) m.instructions.get(index);
                        if (blocksListNode.owner.equals(hm.get("blockJavaClassName")) && blocksListNode.name.equals(hm.get("blocksListFieldName")))
                        {
                            int offset = 1;
                            while (m.instructions.get(index + offset).getOpcode() != ASTORE)
                                offset++;
                            VarInsnNode blockNode = (VarInsnNode) m.instructions.get(index + offset);
                            blockIndex = blockNode.var;
                        }
                    }
                    
                    if (m.instructions.get(index).getType() == AbstractInsnNode.METHOD_INSN)
                    {
                        MethodInsnNode mdNode = (MethodInsnNode) m.instructions.get(index);
                        if (mdNode.owner.equals(hm.get("worldJavaClassName")) && mdNode.name.equals(hm.get("getBlockMetadataMethodName")))
                        {
                            int offset = 1;
                            while (m.instructions.get(index + offset).getOpcode() != ISTORE)
                                offset++;
                            VarInsnNode mdFieldNode = (VarInsnNode) m.instructions.get(index + offset);
                            mdIndex = mdFieldNode.var;
                        }
                    }
                    
                    if (m.instructions.get(index).getOpcode() == IFNULL)
                    {
                        
                        int offset = 1;
                        while (m.instructions.get(index + offset).getOpcode() != ALOAD)
                        {
                        	offset++;
                        }
                        
                        LabelNode lmm1Node = new LabelNode(new Label());
                        LabelNode lmm2Node = new LabelNode(new Label());
                        
                        InsnList toInject = new InsnList();
                        
                        toInject.add(new VarInsnNode(ALOAD, 0));
                        toInject.add(new FieldInsnNode(GETFIELD, (String) hm.get("javaClassName"), (String) hm.get("worldFieldName"), "L" + (String) hm.get("worldJavaClassName") + ";"));
                        toInject.add(new VarInsnNode(ILOAD, 1));
                        toInject.add(new VarInsnNode(ILOAD, 2));
                        toInject.add(new VarInsnNode(ILOAD, 3));
                        toInject.add(new VarInsnNode(ALOAD, blockIndex));
                        toInject.add(new VarInsnNode(ILOAD, mdIndex));
                        toInject.add(new VarInsnNode(ALOAD, 0));
                        toInject.add(new FieldInsnNode(GETFIELD, (String) hm.get("javaClassName"), (String) hm.get("entityPlayerFieldName"), "L" + (String) hm.get("entityPlayerMPJavaClassName") + ";"));
                        toInject.add(new MethodInsnNode(INVOKESTATIC, "com/ForgeEssentials/core/CustomEventFactory", "onBlockHarvested", "(L" + (String) hm.get("worldJavaClassName") + ";IIIL" + (String) hm.get("blockJavaClassName") + ";IL" + (String) hm.get("entityPlayerJavaClassName") + ";)Z"));
                        toInject.add(new JumpInsnNode(IFNE, lmm2Node));
                        toInject.add(new InsnNode(ICONST_0));
                        toInject.add(new InsnNode(IRETURN));
                        toInject.add(lmm2Node);
                        toInject.add(lmm1Node);
                        
                        m.instructions.insertBefore(m.instructions.get(index + offset), toInject);
                         
                        msg("[FE coremod] Patching ItemInWorldManager Complete!");
                        break;
                    }
                }
            }
        }
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
