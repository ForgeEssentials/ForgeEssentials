package com.ForgeEssentials.WorldControl;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly(value = Side.CLIENT)
public class TickHandler implements ITickHandler {
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (Minecraft.getMinecraft().playerController != null
				&& !(Minecraft.getMinecraft().playerController instanceof ExtendedPlayerControllerMP)) {
			Minecraft.getMinecraft().playerController = new ExtendedPlayerControllerMP(
					Minecraft.getMinecraft(), Minecraft.getMinecraft()
							.getSendQueue(),
					Minecraft.getMinecraft().playerController
							.isInCreativeMode());
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		// do nothing.
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return null;
	}
}
