package com.ForgeEssentials;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.SidedProxy;

@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = "0.0.1")
public class Main
{
	@SidedProxy(clientSide = "com.ForgeEssentials.ProxyClient", serverSide = "com.ForgeEssentials.ProxyCommon")
	public static ProxyCommon proxy;
	
	@Instance(value="ForgeEssentials")
	public static Main instance;
	
	@PreInit
	public void preInit()
	{
		
	}
	
	@Init
	public void load()
	{
		
	}
	
	@ServerStarted
	public void serverStart()
	{
		
	}

}
