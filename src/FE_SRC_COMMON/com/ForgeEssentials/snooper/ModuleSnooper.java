package com.ForgeEssentials.snooper;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.rcon.IServer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.permission.ForgeEssentialsPermissionRegistrationEvent;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.util.OutputHandler;

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

	public static boolean autoReboot;
	
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
			server = e.getServer();
			startQuery();
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
    
    public static void startQuery()
    {
    	if(theThread != null)
    	{
    		ModuleSnooper.theThread.closeAllSockets_do(true);
    		ModuleSnooper.theThread.running = false;
    	}
    	theThread = new RConQueryThread((IServer) server);
		theThread.startThread();
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
