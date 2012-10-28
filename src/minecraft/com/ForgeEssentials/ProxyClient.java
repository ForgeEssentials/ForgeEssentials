package com.ForgeEssentials;

import com.ForgeEssentials.WorldControl.GuiSelectionBox;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@SideOnly(value=Side.CLIENT)
public class ProxyClient extends ProxyCommon
{
	@Override
	public void load(FMLInitializationEvent e)
	{
		MinecraftForge.EVENT_BUS.register(new GuiSelectionBox());
	}
}
