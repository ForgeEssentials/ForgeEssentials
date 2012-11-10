package com.ForgeEssentials.client.core;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.core.ProxyCommon;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;

@SideOnly(value=Side.CLIENT)
public class ProxyClient extends ProxyCommon
{
	@Override
	public void load(FMLInitializationEvent e)
	{
		MinecraftForge.EVENT_BUS.register(new CUIRenderrer());
	}
}
