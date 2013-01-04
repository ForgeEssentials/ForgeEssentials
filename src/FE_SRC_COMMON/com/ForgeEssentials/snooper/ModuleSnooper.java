package com.ForgeEssentials.snooper;

import java.util.ArrayList;

import net.minecraft.network.rcon.IServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.snooper.API.API;
import com.ForgeEssentials.snooper.response.PlayerArmor;
import com.ForgeEssentials.snooper.response.PlayerInfoResonce;
import com.ForgeEssentials.snooper.response.PlayerInv;
import com.ForgeEssentials.snooper.response.PlayerList;
import com.ForgeEssentials.snooper.response.ServerInfo;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
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

	public static boolean autoReboot;
	
	public ModuleSnooper()
	{
		OutputHandler.SOP("Snooper module is enabled. Loading...");
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void serverStarting(FMLServerStartingEvent e) 
	{
		API.registerResponce(0, new ServerInfo());
		API.registerResponce(1, new PlayerList());
		
		API.registerResponce(5, new PlayerInfoResonce());
		API.registerResponce(6, new PlayerArmor());
		API.registerResponce(7, new PlayerInv());
		
		e.registerServerCommand(new CommandReloadQuery());
		
		configSnooper = new ConfigSnooper();
	}
	
	@ForgeSubscribe
	public void registerPermissions(PermissionRegistrationEvent event)
	{
		event.registerPermissionDefault("ForgeEssentials.Snooper.commands", false);
		event.registerPermissionDefault("ForgeEssentials.Snooper.commands.reloadquery", false);
		
		event.registerPerm(PermissionsAPI.GROUP_OWNERS, "ForgeEssentials.commands.reloadquery", true);
	}
    
    public static void startQuery()
    {
    	try
    	{
    		if(theThread != null)
    		{
    			ModuleSnooper.theThread.closeAllSockets_do(true);
    			ModuleSnooper.theThread.running = false;
    		}
    		if(enable)
    		{
    			theThread = new RConQueryThread((IServer) FMLCommonHandler.instance().getMinecraftServerInstance());
    			theThread.startThread();
    		}
    	}
    	catch(Exception e){} 
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
