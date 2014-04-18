package com.forgeessentials.client;

import java.util.logging.Logger;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;

import com.forgeessentials.client.cui.CUIPlayerLogger;
import com.forgeessentials.client.cui.CUIRenderrer;
import com.forgeessentials.client.cui.CUIRollback;
import com.forgeessentials.client.gui.FEKeyBinding;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@NetworkMod(clientSideRequired = false, serverSideRequired = false, clientPacketHandlerSpec = @SidedPacketHandler(channels =
{ "ForgeEssentials" }, packetHandler = com.forgeessentials.client.network.PacketHandler.class))
@Mod(modid = "ForgeEssentialsClient", name = "Forge Essentials Client Addon", version = "%VERSION%")
public class ForgeEssentialsClient
{

	private boolean allowCUI;
	
	public static Logger feclientlog;
	
	@SideOnly(Side.CLIENT)
	private static PlayerInfoClient	info;

	private boolean getDevOverride() 
	{
		String prop = System.getProperty("forgeessentials.developermode");
		if (prop != null && prop.equals("true")){ // FOR DEVS ONLY! THAT IS WHY IT IS A PROPERTY!!!
		
			feclientlog.severe("Developer mode has been enabled, things may break.");
			return true;
		}
		else
			return false;
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		feclientlog = e.getModLog();
		
		if (FMLCommonHandler.instance().getSide().isServer() && getDevOverride() == false)
			throw new RuntimeException("ForgeEssentialsClient should not be installed on a server!");
		
		if (FMLCommonHandler.instance().getSide().isClient()){
			config(new Configuration(e.getSuggestedConfigurationFile()));
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void config(Configuration config){
		config.load();
		config.addCustomCategoryComment("Core", "Configure ForgeEssentials .");

		Property prop = config.get("Core", "allowCUI", true);
		prop.comment = "Set to false to disable graphical selections.";
		allowCUI = prop.getBoolean(true);
		
		prop = config.get("Core", "keybinding", 88);
		prop.comment = "Minecraft key code for activating the FE GUI. Defaults to F12 (88)";
		FEKeyBinding.fekeycode = prop.getInt(88);
		// any other parts please config here
		config.save();
	}
	

	@SideOnly(Side.CLIENT)
	@EventHandler
	public void load(FMLInitializationEvent e)
	{
		NetworkRegistry.instance().registerConnectionHandler(new ClientConnectionHandler());
		if (allowCUI){
			MinecraftForge.EVENT_BUS.register(new CUIRenderrer());
			MinecraftForge.EVENT_BUS.register(new CUIPlayerLogger());
			MinecraftForge.EVENT_BUS.register(new CUIRollback());
		}
        KeyBindingRegistry.registerKeyBinding(new FEKeyBinding(new KeyBinding[] { new KeyBinding("ForgeEssentials Menu", 88) }));
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
