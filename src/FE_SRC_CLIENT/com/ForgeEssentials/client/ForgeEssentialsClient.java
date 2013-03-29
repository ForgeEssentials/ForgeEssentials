package com.ForgeEssentials.client;

import java.util.logging.Logger;

import com.ForgeEssentials.client.cui.CUIPlayerLogger;
import com.ForgeEssentials.client.cui.CUIRenderrer;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@NetworkMod(clientSideRequired = false, serverSideRequired = false, clientPacketHandlerSpec = @SidedPacketHandler(channels =
{ "ForgeEssentials" }, packetHandler = com.ForgeEssentials.client.network.PacketHandler.class))
@Mod(modid = "ForgeEssentialsClient", name = "Forge Essentials Client Addon", version = "@VERSION@")
public class ForgeEssentialsClient
{

	private boolean allowCUI;
	
	public Logger feclientlog;
	
	private static final String beta = "@BETA@";
	
	@SideOnly(Side.CLIENT)
	private static PlayerInfoClient	info;

	private boolean getDevOverride() 
	{
		if (System.getProperty("forgeessentials.client.developermode").equals("true")){ // FOR DEVS ONLY! THAT IS WHY IT IS A PROPERTY!!!
		
			feclientlog.severe("Developer mode has been enabled, things may break.");
			return true;
		}
		else return false;
	}
	
	@PreInit
	public void preInit(FMLPreInitializationEvent e)
	{
		if (FMLCommonHandler.instance().getSide().isServer() && getDevOverride() == false)
			throw new RuntimeException("ForgeEssentialsClient should not be installed on a server!");
		
		feclientlog = e.getModLog();
		
		if (beta.equals("true")){
			feclientlog.fine("You are running ForgeEssentials beta build @VERSION@");
			feclientlog.fine("Please report all bugs to the github issue tracker at https://github.com/ForgeEssentials/ForgeEssentialsMain/issues.");
			feclientlog.fine("We thank you for helping us to beta test ForgeEssentials.");
		}
		
		Configuration config = new Configuration(e.getSuggestedConfigurationFile());
		config.load();
		config.addCustomCategoryComment("Core", "Configure ForgeEssentials .");

		Property prop = config.get("Core", "allowCUI", true);
		prop.comment = "Set to false to disable graphical selections.";
		allowCUI = prop.getBoolean(true);
		// any other parts please config here
		config.save();
		
	}

	@SideOnly(Side.CLIENT)
	@Init
	public void load(FMLInitializationEvent e)
	{
		NetworkRegistry.instance().registerConnectionHandler(new ClientConnectionHandler());
		if (allowCUI){
			MinecraftForge.EVENT_BUS.register(new CUIRenderrer());
			MinecraftForge.EVENT_BUS.register(new CUIPlayerLogger());
		}
	}

	@SideOnly(Side.CLIENT)
	public static PlayerInfoClient getInfo()
	{
		if (info == null)
			info = new PlayerInfoClient();
		return info;
	}

	@SideOnly(Side.CLIENT)
	public static void setInfo(PlayerInfoClient info)
	{
		ForgeEssentialsClient.info = info;
	}

}
