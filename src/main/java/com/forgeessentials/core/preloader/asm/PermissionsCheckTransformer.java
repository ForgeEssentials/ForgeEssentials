package com.forgeessentials.core.preloader.asm;

import com.forgeessentials.core.preloader.Data;

import net.minecraft.launchwrapper.IClassTransformer;

// NOT IMPLEMENTED YET
public class PermissionsCheckTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (Data.cmdClassesdev.contains(name)){
			return transformClass(name, bytes, "canCommandSenderUseCommand");
		}
		if (Data.cmdClassesob.contains(name)){
			return transformClass(name, bytes, Data.cCSUC);
		}
		return bytes;
	}

	private byte[] transformClass(String name, byte[] bytes, String string) {
		return bytes;
		
	}

}
