package com.ForgeEssentials.snooper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.rcon.IServer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
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
	public static boolean enable;
	
	public static RConQueryThread theThread;
	private static ArrayList<String> names;

	private static MinecraftServer server;
	
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
     * Get all of the info!
     * @param username
     * @return All of the date in this format: [key1;value1, key2;value2]
     */
    public static HashMap<String, String> getUserData(String username) 
	{
    	HashMap<String, String> PlayerData = new HashMap();
    	EntityPlayer player = server.getConfigurationManager().getPlayerForUsername(username);
		
    	PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
		if(pi != null)
		{
			if(pi.home != null) PlayerData.put("home", TextFormatter.pointToJSON(pi.home));
			if(pi.lastDeath != null) PlayerData.put("lastDeath", TextFormatter.pointToJSON(pi.lastDeath));
		}
		
		PlayerData.put("wallet", ""+Wallet.getWallet(player));
		PlayerData.put("health", ""+player.getHealth());
		PlayerData.put("food", ""+player.getFoodStats());
		PlayerData.put("pos", TextFormatter.pointToJSON(new WorldPoint(player)));
		
		HashMap<String, String> temp = new HashMap();
		for(Object effectObj : player.getActivePotionEffects())
		{
			PotionEffect effect = ((PotionEffect) effectObj);
			HashMap<String, String> temp2 = new HashMap();
			temp2.put("name", "" + effect.getEffectName());
			temp2.put("amp", "" + effect.getAmplifier());
			temp2.put("dur", "" + effect.getDuration());
		
			temp.put("" + effect.getPotionID(), TextFormatter.mapToJSON(temp2));
		}
		PlayerData.put("potion", TextFormatter.mapToJSON(temp));
		temp.clear();
		
		for(ItemStack stack: player.inventory.armorInventory)
		{
			if(stack != null)
			{
				temp.put(stack.getDisplayName(), TextFormatter.itemStackToJSON(stack));
			}
		}
		PlayerData.put("armor", TextFormatter.mapToJSON(temp));
		
		return PlayerData;
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
