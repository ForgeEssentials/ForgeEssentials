package com.ForgeEssentials;

import java.io.File;

import com.ForgeEssentials.WorldControl.WorldControlMain;
import com.ForgeEssentials.network.HandlerClient;
import com.ForgeEssentials.network.HandlerServer;
import com.ForgeEssentials.permissions.FEPermissionHandler;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;

@NetworkMod(
		clientSideRequired = false,
		serverSideRequired = false,
		clientPacketHandlerSpec = @SidedPacketHandler(channels = {"ForgeEssentials", "WorldControl"}, packetHandler = HandlerClient.class),
		serverPacketHandlerSpec = @SidedPacketHandler(channels = {"ForgeEssentials", "WorldControl"}, packetHandler = HandlerServer.class)
		)
@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = "0.0.1")
public class Main
{
	@SidedProxy(clientSide = "com.ForgeEssentials.ProxyClient", serverSide = "com.ForgeEssentials.ProxyCommon")
	public static ProxyCommon proxy;
	
	@Instance(value="ForgeEssentials")
	public static Main instance;
	
	public static final File FEDIR = new File("./ForgeEssentials/");
	
	public FEPermissionHandler pHandler;
	public WorldControlMain worldControl;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent e)
	{
		if (!FEDIR.exists())
			FEDIR.mkdir();
		
		worldControl = new WorldControlMain();
		worldControl.preLoad(e);
		
		// configs.
		Configuration config = new Configuration(new File(FEDIR, "config.txt"));
		config.load();
		WorldControlMain.wandID = config.get("Wand", "Wand ID", 271).getInt();
		config.save();
	}
	
	@Init
	public void load(FMLInitializationEvent e)
	{
		proxy.load(e);
		worldControl.load(e);
		pHandler = new FEPermissionHandler();
		MinecraftForge.EVENT_BUS.register(pHandler);
	}
	
	@ServerStarting 
	public void serverStart(FMLServerStartingEvent e)
	{
		worldControl.serverLoad(e);
	}

}
