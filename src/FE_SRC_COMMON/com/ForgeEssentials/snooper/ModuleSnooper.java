package com.ForgeEssentials.snooper;

import java.io.File;
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
     * @return All of the date!
     */
    public static HashMap<String, String> getUserData(EntityPlayerMP player) 
	{
    	HashMap<String, String> PlayerData = new HashMap();
    	HashMap<String, String> tempMap = new HashMap();
    	ArrayList<String> tempArgs = new ArrayList();
    	String username = player.username;
		
    	PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
		if(pi != null)
		{
			if(pi.home != null) PlayerData.put("home", TextFormatter.pointToJSON(pi.home));
			if(pi.lastDeath != null) PlayerData.put("lastDeath", TextFormatter.pointToJSON(pi.lastDeath));
		}
		
		PlayerData.put("armor", "" + player.inventory.getTotalArmorValue());
		PlayerData.put("wallet", "" + Wallet.getWallet(player));
		PlayerData.put("health", "" + player.getHealth());
		PlayerData.put("pos", TextFormatter.pointToJSON(new WorldPoint(player)));
		PlayerData.put("potion", TextFormatter.potionsToJSON(player.getActivePotionEffects()));
		PlayerData.put("ping", "" + player.ping);
		PlayerData.put("gm", player.theItemInWorldManager.getGameType().getName());
		
		{
			tempMap.clear();
			tempMap.put("lvl", "" + player.experienceLevel);
			tempMap.put("bar", "" + player.experience);
		}
		PlayerData.put("xp", TextFormatter.mapToJSON(tempMap));
		
		{
			tempMap.clear();
			tempMap.put("food", "" + player.getFoodStats().getFoodLevel());
			tempMap.put("saturation", "" + player.getFoodStats().getSaturationLevel());
		}
		PlayerData.put("foodStats", TextFormatter.mapToJSON(tempMap));
		
		{
			tempMap.clear();
			tempMap.put("edit", "" + player.capabilities.allowEdit);
			tempMap.put("allowFly", "" + player.capabilities.allowFlying);
			tempMap.put("isFly", "" + player.capabilities.isFlying);
			tempMap.put("noDamage", "" + player.capabilities.disableDamage);
		}
		PlayerData.put("capabilities", TextFormatter.mapToJSON(tempMap));
		
		return PlayerData;
	}
    
    /**
     * Get all of the armor data
     * @param player
     * @return
     */
    public static HashMap<String, String> getArmorData(EntityPlayerMP player) 
	{
    	HashMap<String, String> PlayerData = new HashMap();
    	String username = player.username;

    	ItemStack stack = player.inventory.armorInventory[3];
    	if(stack != null)
    	{
    		PlayerData.put(stack.getDisplayName(), TextFormatter.itemStackToJSON(stack, true));
    	}

    	stack = player.inventory.armorInventory[2];
    	if(stack != null)
    	{
    		PlayerData.put(stack.getDisplayName(), TextFormatter.itemStackToJSON(stack, true));
    	}
    	
    	stack = player.inventory.armorInventory[1];
    	if(stack != null)
    	{
    		PlayerData.put(stack.getDisplayName(), TextFormatter.itemStackToJSON(stack, true));
    	}

    	stack = player.inventory.armorInventory[0];
    	if(stack != null)
    	{
    		PlayerData.put(stack.getDisplayName(), TextFormatter.itemStackToJSON(stack, true));
    	}
    	
		return PlayerData;
	}
    
    /**
     * Get all of the inv data
     * @param player
     * @return
     */
    public static HashMap<String, String> getInvData(EntityPlayerMP player) 
	{
    	HashMap<String, String> PlayerData = new HashMap();
    	String username = player.username;

    	int i = 0;
    	for(ItemStack stack : player.inventory.mainInventory)
    	{
    		if(stack != null)
        	{
        		PlayerData.put(stack.getDisplayName(), TextFormatter.itemStackToJSON(stack, false));
        		i ++;
        	}
    	}
    	
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
