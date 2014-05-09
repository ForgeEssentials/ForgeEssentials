package com.forgeessentials.core.preloader.asm;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import com.forgeessentials.core.preloader.Data;

import net.minecraft.launchwrapper.IClassTransformer;

// NOT IMPLEMENTED YET
public class PermissionsCheckTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (Data.cmdClassesdev.contains(name)){
			return transformClass(name, bytes, "canCommandSenderUseCommand", "net/minecraft/command/ICommandSender");
		}
		if (Data.cmdClassesob.contains(name)){
			return transformClass(name, bytes, Data.cCSUC, Data.ics);
		}
		return bytes;
	}

	private byte[] transformClass(String name, byte[] bytes, String methodName, String icsClassName) {
		/*
		 * ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		Iterator<MethodNode> methods = classNode.methods.iterator();
		
		while (methods.hasNext())
		{
			MethodNode m = methods.next();

			if (m.name.equals(methodName) && m.desc.equals("(L" + icsClassName") + ";"))
			{
			System.out.println("[FE coremod] Found target method " + m.name + m.desc + "!");
			int offset = 0;
                while (m.instructions.get(offset).getOpcode() != Opcodes.ALOAD)
                {
                    offset++;
                }
                
                LabelNode lmm1Node = new LabelNode(new Label());
                LabelNode lmm2Node = new LabelNode(new Label());

                InsnList toInject = new InsnList();
		 */
		return bytes;
		
	}

}
