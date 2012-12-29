package com.ForgeEssentials.snooper;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.rcon.IServer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.WorldBorder.ConfigWorldBorder;
import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.economy.Wallet;
import com.ForgeEssentials.permission.ForgeEssentialsPermissionRegistrationEvent;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class ModuleSnooper implements IFEModule
{
	public static ConfigSnooper configSnooper;
	
	public static int port;
	public static String hostname;
	public static boolean enable;
	
	public static RConQueryThread theThread;
	private static ArrayList<String> names;

	private static MinecraftServer server;

	public static boolean overrideIP;
	public static String overrideIPValue;
	
	public ModuleSnooper()
	{
		OutputHandler.SOP("Snooper module is enabled. Loading...");
		configSnooper = new ConfigSnooper();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void serverStarting(FMLServerStartingEvent e) 
	{
		if(enable)
		{
			e.registerServerCommand(new CommandReloadQuery());
			theThread = new RConQueryThread((IServer) e.getServer());
			theThread.startThread();
			server = e.getServer();
		}
	}
	
	@ForgeSubscribe
	public void registerPermissions(ForgeEssentialsPermissionRegistrationEvent event)
	{
		event.registerPermissionDefault("ForgeEssentials.Snooper.commands", false);
		event.registerPermissionDefault("ForgeEssentials.Snooper.commands.reloadquery", false);
		
		event.registerGlobalGroupPermissions(PermissionsAPI.GROUP_OWNERS, "ForgeEssentials.commands.reloadquery", true);
	}
    
    /**
     * Get all of the inv data
     * @param player
     * @return
     */
    public static ArrayList<String> getInvData(EntityPlayerMP player) 
	{
    	ArrayList<String> tempArgs = new ArrayList();
    	String username = player.username;

    	for(ItemStack stack : player.inventory.mainInventory)
    	{
    		if(stack != null)
        	{
    			tempArgs.add(TextFormatter.toJSON(stack, false));
        	}
    	}
		return tempArgs;
	}
    

	/*
	 * Not needed
	 */
	
	@Override
	public void load(FMLInitializationEvent e){}

	@Override
	public void postLoad(FMLPostInitializationEvent e){}

	@Override
	public void serverStopping(FMLServerStoppingEvent e) {}
	
	@Override
	public void serverStarted(FMLServerStartedEvent e) {}
	
	@Override
	public void preLoad(FMLPreInitializationEvent e) 
	{}
}
