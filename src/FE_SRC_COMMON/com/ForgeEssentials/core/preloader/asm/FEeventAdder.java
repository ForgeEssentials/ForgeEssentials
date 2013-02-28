package com.ForgeEssentials.core.preloader.asm;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISTORE;

import java.util.HashMap;
import java.util.Iterator;

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

import cpw.mods.fml.relauncher.IClassTransformer;

public class FEeventAdder implements IClassTransformer
{
	public static HashMap<String, String>	iiwmHMob	= makeiiwmHMob();
	public static HashMap<String, String>	iiwmHMdev	= makeiiwmHMdev();

	public static HashMap<String, String>	isHMob		= makeisHMob();
	public static HashMap<String, String>	isHMdev		= makeisHMdev();

	public static boolean					addedBreak	= false;
	public static boolean					addedPlace	= false;

	public static HashMap makeiiwmHMob()
	{
		HashMap iiwmHMob = new HashMap();

		iiwmHMob.put("className", "ir");
		iiwmHMob.put("javaClassName", "ir");
		iiwmHMob.put("targetMethodName", "d");
		iiwmHMob.put("worldFieldName", "a");
		iiwmHMob.put("entityPlayerFieldName", "b");
		iiwmHMob.put("worldJavaClassName", "yc");
		iiwmHMob.put("getBlockMetadataMethodName", "h");
		iiwmHMob.put("blockJavaClassName", "amq");
		iiwmHMob.put("blocksListFieldName", "p");
		iiwmHMob.put("entityPlayerJavaClassName", "qx");
		iiwmHMob.put("entityPlayerMPJavaClassName", "iq");

		return iiwmHMob;
	}

	public static HashMap makeiiwmHMdev()
	{
		HashMap iiwmHMdev = new HashMap<String, String>();

		iiwmHMdev.put("className", "net.minecraft.item.ItemInWorldManager");
		iiwmHMdev.put("javaClassName", "net/minecraft/item/ItemInWorldManager");
		iiwmHMdev.put("targetMethodName", "removeBlock");
		iiwmHMdev.put("worldFieldName", "theWorld");
		iiwmHMdev.put("entityPlayerFieldName", "thisPlayerMP");
		iiwmHMdev.put("worldJavaClassName", "net/minecraft/world/World");
		iiwmHMdev.put("getBlockMetaiiwmHMdevMethodName", "getBlockMetaiiwmHMdev");
		iiwmHMdev.put("blockJavaClassName", "net/minecraft/block/Block");
		iiwmHMdev.put("blocksListFieldName", "blocksList");
		iiwmHMdev.put("entityPlayerJavaClassName", "net/minecraft/entity/player/EntityPlayer");
		iiwmHMdev.put("entityPlayerMPJavaClassName", "net/minecraft/entity/player/EntityPlayerMP");

		return iiwmHMdev;
	}

	public static HashMap makeisHMob()
	{
		HashMap isHMob = new HashMap();

		isHMob.put("className", "ur");
		isHMob.put("javaClassName", "ur");
		isHMob.put("targetMethodName", "a");
		isHMob.put("itemstackJavaClassName", "ur");
		isHMob.put("entityPlayerJavaClassName", "qx");
		isHMob.put("worldJavaClassName", "yc");

		return isHMob;
	}

	public static HashMap makeisHMdev()
	{
		HashMap isHMdev = new HashMap<String, String>();

		isHMdev.put("className", "net.minecraft.item.ItemStack");
		isHMdev.put("javaClassName", "net/minecraft/item/ItemStack");
		isHMdev.put("targetMethodName", "tryPlaceItemIntoWorld");

		isHMdev.put("itemstackJavaClassName", "net/minecraft/item/ItemStack");
		isHMdev.put("entityPlayerJavaClassName", "net/minecraft/entity/player/EntityPlayer");
		isHMdev.put("worldJavaClassName", "net/minecraft/world/World");

		return isHMdev;
	}

	@Override
	public byte[] transform(String name, byte[] bytes)
	{
		if (name.equals(iiwmHMob.get("className")))
			// ItemInWorldManager, Obfuscated
			return transformItemInWorldManager(bytes, iiwmHMob);

		if (name.equals(iiwmHMdev.get("className")))
			// ItemInWorldManager, NOT Obfuscated
			return transformItemInWorldManager(bytes, iiwmHMdev);

		if (name.equals(isHMob.get("className")))
			// ItemStack, Obfuscated
			return transformItemStack(bytes, isHMob);

		if (name.equals(isHMdev.get("className")))
			// ItemStack, NOT Obfuscated
			return transformItemStack(bytes, isHMdev);
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
				toInject.add(new MethodInsnNode(INVOKESTATIC, "com/ForgeEssentials/util/events/ForgeEssentialsEventFactory", "onBlockPlace", "(L" + hm.get("itemstackJavaClassName") + ";L" + hm.get("entityPlayerJavaClassName") + ";L" + hm.get("worldJavaClassName")
						+ ";IIIIFFF)Z"));
				toInject.add(new JumpInsnNode(IFNE, lmm2Node));
				toInject.add(new InsnNode(ICONST_0));
				toInject.add(new InsnNode(IRETURN));
				toInject.add(lmm2Node);
				toInject.add(lmm1Node);

				m.instructions.insertBefore(m.instructions.get(offset), toInject);
				addedPlace = true;
				msg("[FE coremod] Patching ItemInWorldManager Complete!");
				break;
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	private byte[] transformItemInWorldManager(byte[] bytes, HashMap<String, String> hm)
	{
		msg("[FE coremod] Patching ItemInWorldManager...");

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
							{
								offset++;
							}
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
							{
								offset++;
							}
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
						toInject.add(new FieldInsnNode(GETFIELD, hm.get("javaClassName"), hm.get("worldFieldName"), "L" + hm.get("worldJavaClassName") + ";"));
						toInject.add(new VarInsnNode(ILOAD, 1));
						toInject.add(new VarInsnNode(ILOAD, 2));
						toInject.add(new VarInsnNode(ILOAD, 3));
						toInject.add(new VarInsnNode(ALOAD, blockIndex));
						toInject.add(new VarInsnNode(ILOAD, mdIndex));
						toInject.add(new VarInsnNode(ALOAD, 0));
						toInject.add(new FieldInsnNode(GETFIELD, hm.get("javaClassName"), hm.get("entityPlayerFieldName"), "L" + hm.get("entityPlayerMPJavaClassName") + ";"));
						toInject.add(new MethodInsnNode(INVOKESTATIC, "com/ForgeEssentials/util/events/ForgeEssentialsEventFactory", "onBlockHarvested", "(L" + hm.get("worldJavaClassName") + ";IIIL" + hm.get("blockJavaClassName") + ";IL"
								+ hm.get("entityPlayerJavaClassName") + ";)Z"));
						toInject.add(new JumpInsnNode(IFNE, lmm2Node));
						toInject.add(new InsnNode(ICONST_0));
						toInject.add(new InsnNode(IRETURN));
						toInject.add(lmm2Node);
						toInject.add(lmm1Node);

						m.instructions.insertBefore(m.instructions.get(index + offset), toInject);
						addedBreak = true;
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

	public static void msg(String msg)
	{
		System.out.println(msg);
	}

}
