package com.ForgeEssentials;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.WorldControl.WorldControl;
import com.ForgeEssentials.commands.*;
import com.ForgeEssentials.network.ConnectionHandler;
import com.ForgeEssentials.network.HandlerClient;
import com.ForgeEssentials.network.HandlerServer;
import com.ForgeEssentials.permissions.FEPermissionHandler;

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

@NetworkMod(clientSideRequired = false, serverSideRequired = false, connectionHandler = ConnectionHandler.class, clientPacketHandlerSpec = @SidedPacketHandler(channels = { "ForgeEssentials", "WorldControl" }, packetHandler = HandlerClient.class), serverPacketHandlerSpec = @SidedPacketHandler(channels = { "ForgeEssentials", "WorldControl" }, packetHandler = HandlerServer.class))
@Mod(modid = "ForgeEssentials", name = "Forge Essentials", version = "0.0.1")
public class ForgeEssentials
{
	@SidedProxy(clientSide = "com.ForgeEssentials.ProxyClient", serverSide = "com.ForgeEssentials.ProxyCommon")
	public static ProxyCommon proxy;

	@Instance(value = "ForgeEssentials")
	public static ForgeEssentials instance;

	public static final File FEDIR = new File("./ForgeEssentials/");
	public static final File FECONFIG = new File(FEDIR, "config.txt");

	public FEPermissionHandler pHandler;
	public WorldControl worldcontrol;
	public static FEConfig config;

	public static String motd;

	@PreInit
	public void preInit(FMLPreInitializationEvent e)
	{
		if (!FEDIR.exists())
			FEDIR.mkdir();
		
		config = new FEConfig();
		
		worldcontrol = new WorldControl();
		worldcontrol.preLoad(e);

		// configs.
		Configuration config = new Configuration(new File(FEDIR, "config.txt"));
		config.load();
		motd = config.get("Basic", "MOTD", "ForgeEssentials is awesome. https://github.com/ForgeEssentials/ForgeEssentialsMain").value;
		config.save();
	}

	@Init
	public void load(FMLInitializationEvent e)
	{
		worldcontrol.load(e);
		proxy.load(e);
		pHandler = new FEPermissionHandler();
		MinecraftForge.EVENT_BUS.register(pHandler);
	}

	@ServerStarting
	public void serverStart(FMLServerStartingEvent e)
	{
		// commands
		e.registerServerCommand(new CommandMotd());
		e.registerServerCommand(new CommandButcher());
		
		worldcontrol.serverStarting(e);
	}

}
