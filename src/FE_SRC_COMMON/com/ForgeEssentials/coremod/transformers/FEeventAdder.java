package com.ForgeEssentials.coremod.transformers;

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
import static org.objectweb.asm.Opcodes.LDC;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.ForgeEssentials.coremod.FEPreLoader;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.FMLRelauncher;
import cpw.mods.fml.relauncher.IClassTransformer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FEeventAdder implements IClassTransformer
{
	public static HashMap<String, String> iiwmHMob = makeiiwmHMob();
	public static HashMap<String, String> iiwmHMdev = makeiiwmHMdev();
	
	public static HashMap<String, String> isHMob = makeisHMob();
	public static HashMap<String, String> isHMdev = makeisHMdev();
	
	public static HashMap<String, String> mcsHMob = makemcsHMob();
	public static HashMap<String, String> mcsHMdev = makemcsHMdev();
	
    public static boolean serverbranded = false;
    public static boolean clientbranded = false;
	
	private static final String SERVERBRAND = "forge,fml, ForgeEssentials";
	
	public static boolean addedBreak = false;
	public static boolean addedPlace = false;
	
	public static HashMap makeiiwmHMob()
	{
		HashMap	iiwmHMob = new HashMap();

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
		HashMap	isHMob = new HashMap();
		
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
		{
			// ItemInWorldManager, Obfuscated
			return transformItemInWorldManager(bytes, iiwmHMob);
		}

		if (name.equals(iiwmHMdev.get("className")))
		{
			// ItemInWorldManager, NOT Obfuscated
			return transformItemInWorldManager(bytes, iiwmHMdev);
		}

		if (name.equals(isHMob.get("className")))
		{
			// ItemStack, Obfuscated
			return transformItemStack(bytes, isHMob);
		}

		if (name.equals(isHMdev.get("className")))
		{
			// ItemStack, NOT Obfuscated
			return transformItemStack(bytes, isHMdev);
		}
		if (name.equals(mcsHMob.get("className")))
		{
			// MinecraftServer, Obfuscated
			return transformMinecraftServer(bytes, mcsHMob);
		}
		
		if (name.equals(mcsHMdev.get("className")))
		{
			// MinecraftServer, NOT Obfuscated
			return transformMinecraftServer(bytes, mcsHMdev);
		}
		if (FMLRelauncher.side().equals("CLIENT")){
			// ClientBrandRetriever - not obfed for some reason
			return transformClientBrandRetriever(name, bytes, "net.minecraft.client.ClientBrandRetriever", FEPreLoader.location);
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

			if (m.name.equals(hm.get("targetMethodName"))
					&& m.desc.equals("(L" + hm.get("entityPlayerJavaClassName") + ";L" + hm.get("worldJavaClassName") + ";IIIIFFF)Z"))
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
				toInject.add(new MethodInsnNode(INVOKESTATIC, "com/ForgeEssentials/core/CustomEventFactory", "onBlockPlace", "(L"
						+ hm.get("itemstackJavaClassName") + ";L" + hm.get("entityPlayerJavaClassName") + ";L" + hm.get("worldJavaClassName") + ";IIIIFFF)Z"));
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
						toInject.add(new FieldInsnNode(GETFIELD, hm.get("javaClassName"), hm.get("entityPlayerFieldName"), "L"
								+ hm.get("entityPlayerMPJavaClassName") + ";"));
						toInject.add(new MethodInsnNode(INVOKESTATIC, "com/ForgeEssentials/core/CustomEventFactory", "onBlockHarvested", "(L"
								+ hm.get("worldJavaClassName") + ";IIIL" + hm.get("blockJavaClassName") + ";IL" + hm.get("entityPlayerJavaClassName") + ";)Z"));
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
	public static HashMap makemcsHMob()
	{
		HashMap mcsHMdev = new HashMap<String, String>();
		
		mcsHMdev.put("className", "net.minecraft.server.MinecraftServer");
		mcsHMdev.put("javaClassName", "net/minecraft/server/MinecraftServer");
		mcsHMdev.put("targetMethodName", "getServerModName");
		
		return mcsHMdev;
	}
	public static HashMap makemcsHMdev()
	{
		HashMap mcsHMdev = new HashMap<String, String>();
		
		mcsHMdev.put("className", "fy");
		mcsHMdev.put("javaClassName", "fy");
		mcsHMdev.put("targetMethodName", "getServerModName");
		
		return mcsHMdev;
	}
	private byte[] transformMinecraftServer(byte[] bytes, HashMap<String, String> hm)
	{
		OutputHandler.fine("[FE coremod] Patching MinecraftServer...");
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext())
		{
			MethodNode m = methods.next();
			if(m.name.equals(hm.get("targetMethodName")))
			{
				OutputHandler.fine("[FE coremod] Found target method " + m.name + m.desc + "!");
				
				int offset = 0;
				while (m.instructions.get(offset).getOpcode() != LDC)
				{
					offset++;
				}
				
				InsnList toInject = new InsnList();
				
				toInject.add(new LdcInsnNode(SERVERBRAND));
				
				m.instructions.insertBefore(m.instructions.get(offset), toInject);
				m.instructions.remove(m.instructions.get(offset + 1));
				
				serverbranded = true;
				break;
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	@SideOnly(Side.CLIENT)
	public static byte[] transformClientBrandRetriever(String name, byte[] bytes, String classname, File location)
	{
		if(!name.equals(classname) || !!ObfuscationReflectionHelper.obfuscation)
			return bytes;
		
		try
		{
			ZipFile zip = new ZipFile(location);
			ZipEntry entry = zip.getEntry(name.replace('.', '/')+".class");
			if(entry == null)
				System.out.println(name+" not found in "+location.getName());
			else
			{
				InputStream zin = zip.getInputStream(entry);
				bytes = new byte[(int) entry.getSize()];
				zin.read(bytes);
				zin.close();
				System.out.println(name+" was overriden from "+location.getName());
			}
			zip.close();
			clientbranded = true;
		}
		catch(Exception e)
		{
			throw new RuntimeException("Error overriding "+name+" from "+location.getName(), e);
		}
		return bytes;
	}
	
	public static void msg (String msg){
		System.out.println(msg);
	}

}
