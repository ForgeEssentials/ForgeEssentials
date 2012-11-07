package com.ForgeEssentials.client.core;

import com.ForgeEssentials.core.ProxyCommon;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@SideOnly(value=Side.CLIENT)
public class ProxyClient extends ProxyCommon
{
	@Override
	public void load(FMLInitializationEvent e)
	{
	}
}
