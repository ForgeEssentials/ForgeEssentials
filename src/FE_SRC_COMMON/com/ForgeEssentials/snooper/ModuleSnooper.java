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
			if(pi.home != null) PlayerData.put("home", TextFormatter.toJSON(pi.home));
			if(pi.lastDeath != null) PlayerData.put("lastDeath", TextFormatter.toJSON(pi.lastDeath));
		}
		
		PlayerData.put("armor", "" + player.inventory.getTotalArmorValue());
		PlayerData.put("wallet", "" + Wallet.getWallet(player));
		PlayerData.put("health", "" + player.getHealth());
		PlayerData.put("pos", TextFormatter.toJSON(new WorldPoint(player)));
		PlayerData.put("ping", "" + player.ping);
		PlayerData.put("gm", player.theItemInWorldManager.getGameType().getName());
		
		if(!player.getActivePotionEffects().isEmpty())
		{
			PlayerData.put("potion", TextFormatter.toJSON(player.getActivePotionEffects()));
		}
		
		{
			tempMap.clear();
			tempMap.put("lvl", "" + player.experienceLevel);
			tempMap.put("bar", "" + player.experience);
		}
		PlayerData.put("xp", TextFormatter.toJSON(tempMap));
		
		{
			tempMap.clear();
			tempMap.put("food", "" + player.getFoodStats().getFoodLevel());
			tempMap.put("saturation", "" + player.getFoodStats().getSaturationLevel());
		}
		PlayerData.put("foodStats", TextFormatter.toJSON(tempMap));
		
		{
			tempMap.clear();
			tempMap.put("edit", "" + player.capabilities.allowEdit);
			tempMap.put("allowFly", "" + player.capabilities.allowFlying);
			tempMap.put("isFly", "" + player.capabilities.isFlying);
			tempMap.put("noDamage", "" + player.capabilities.disableDamage);
		}
		PlayerData.put("capabilities", TextFormatter.toJSON(tempMap));
		
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

    	for(int i = 0; i < 3; i++)
    	{
    		System.out.println(i);
    		ItemStack stack = player.inventory.armorInventory[i];
        	if(stack != null)
        	{
        		PlayerData.put("" + i, TextFormatter.toJSON(stack, true));
        	}
    	}
    	
		return PlayerData;
	}
    
    public static HashMap<String, String> getTPS()
    {
    	HashMap<String, String> data = new HashMap();
    	for (Integer id : DimensionManager.getIDs())
    	{
    		data.put("dim " + id, "" + getTPSFromData(server.worldTickTimes.get(id)));
    	}
    	return data;
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
     * TPS needed functions
     */
	
	private static final DecimalFormat DF = new DecimalFormat("########0.000");
	/**
	 * 
	 * @param par1ArrayOfLong
	 * @return amount of time for 1 tick in ms
	 */
	private static double func_79015_a(long[] par1ArrayOfLong)
    {
        long var2 = 0L;
        long[] var4 = par1ArrayOfLong;
        int var5 = par1ArrayOfLong.length;

        for (int var6 = 0; var6 < var5; ++var6)
        {
            long var7 = var4[var6];
            var2 += var7;
        }

        return (((double)var2 / (double)par1ArrayOfLong.length) * 1.0E-6D);
    }
    
	public static String getTPSFromData(long[] par1ArrayOfLong)
	{
		double tps = (func_79015_a(par1ArrayOfLong)); 
		if(tps < 50)
		{
			return "20";
		}
		else
		{
			return DF.format((1000/tps));
		}
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
