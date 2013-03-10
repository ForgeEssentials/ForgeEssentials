package com.ForgeEssentials.core.preloader.asm;

import static org.objectweb.asm.Opcodes.LDC;

import java.util.HashMap;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.relauncher.FMLRelauncher;
import cpw.mods.fml.relauncher.IClassTransformer;

public class FEBrandingTransformer implements IClassTransformer
{
	public static HashMap<String, String>	mcsHM		= makemcsHM();

	public static HashMap<String, String>	cbrHM		= makecbrHM();

	private static final String				SERVERBRAND	= "forge,fml, ForgeEssentials";

	public static HashMap<String, String> makemcsHM()
	{
		HashMap<String, String> mcsHM = new HashMap<String, String>();

		mcsHM.put("className", "net.minecraft.server.MinecraftServer");
		mcsHM.put("javaClassName", "net/minecraft/server/MinecraftServer");
		mcsHM.put("targetMethodName", "getServerModName");

		return mcsHM;
	}

	public static HashMap<String, String> makecbrHM()
	{
		HashMap<String, String> cbrHM = new HashMap<String, String>();

		cbrHM.put("className", "net.minecraft.client.ClientBrandRetriever");
		cbrHM.put("javaClassName", "net/minecraft/client/ClientBrandRetriever");
		cbrHM.put("targetMethodName", "getClientModName");

		return cbrHM;
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (name.equals(mcsHM.get("className")))
			// MinecraftServer, NOT Obfuscated
			return transformBranding(bytes, mcsHM);
		if (FMLRelauncher.side().equals("CLIENT"))
		{
			if (name.equals(cbrHM.get("className")))
				// ClientBrandRetriever, NOT obfuscated
				return transformBranding(bytes, cbrHM);
		}
		return bytes;
	}

	private byte[] transformBranding(byte[] bytes, HashMap<String, String> hm)
	{
		System.out.println("[FE coremod] Patching MinecraftServer or ClientBrandRetriever...");

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext())
		{
			MethodNode m = methods.next();
			if (m.name.equals(hm.get("targetMethodName")))
			{
				System.out.println("[FE coremod] Found target method " + m.name + m.desc + "!");

				int offset = 0;
				while (m.instructions.get(offset).getOpcode() != LDC)
				{
					offset++;
				}

				InsnList toInject = new InsnList();

				toInject.add(new LdcInsnNode(SERVERBRAND));

				m.instructions.insertBefore(m.instructions.get(offset), toInject);
				m.instructions.remove(m.instructions.get(offset + 1));
				break;
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}

}
