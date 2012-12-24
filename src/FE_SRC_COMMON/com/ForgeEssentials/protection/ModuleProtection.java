package com.ForgeEssentials.protection;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.permission.ForgeEssentialsPermissionRegistrationEvent;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.Zone;
import com.ForgeEssentials.permission.ZoneManager;
import com.ForgeEssentials.permission.query.PermQuery;
import com.ForgeEssentials.permission.query.PermQueryPlayerZone;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

/**
 * @author Dries007
 */

public class ModuleProtection implements IFEModule
{
	public static ConfigProtection config;
	
	public ModuleProtection()
	{
		
		OutputHandler.SOP("Protection module is enabled. Loading...");
		config = new ConfigProtection();
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@ForgeSubscribe
	public void playerInteractin(PlayerInteractEvent e)
	{
		String perm = "ForgeEssentials.allowedit";
		
		if(e.action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
		{
			perm += ".leftclick";
		}
		
		if(e.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) || e.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_AIR))
		{
			perm += ".rightclick";
		}
		
		System.out.println("## Perm: " + perm);
		
		Point point = new Point(e.x, e.y, e.z);
		Zone zone = ZoneManager.getWhichZoneIn(point, e.entityPlayer.worldObj);
		
		System.out.println("## Zone: " + zone.getZoneID());
		
		PermQuery query = new PermQueryPlayerZone(e.entityPlayer, perm, zone);
		boolean result = PermissionsAPI.checkPermAllowed(query);
		
		System.out.println("## Result: " + result);
		
		if(!result)
		{
			e.setCanceled(true);
		}
	}

	/*
	 * Module part
	 */
	
	@Override
	public void preLoad(FMLPreInitializationEvent e){}

	@Override
	public void load(FMLInitializationEvent e){}

	@Override
	public void postLoad(FMLPostInitializationEvent e){}

	@Override
	public void serverStopping(FMLServerStoppingEvent e){}
	
	@Override
	public void serverStarting(FMLServerStartingEvent e){}

	@Override
	public void serverStarted(FMLServerStartedEvent e){}

	@ForgeSubscribe
	public void registerPermissions(ForgeEssentialsPermissionRegistrationEvent event)
	{
		System.out.println("SDSGFDJDGSDKFGDFSGLKGN");
		
		event.registerPermissionDefault("ForgeEssentials.allowedit", false);
		
		event.registerPermissionDefault("ForgeEssentials.allowedit.leftclick", false);
		event.registerPermissionDefault("ForgeEssentials.allowedit.rightclick", false);
		
		event.registerGlobalGroupPermissions(PermissionsAPI.GROUP_DEFAULT, "ForgeEssentials.allowedit.leftclick", false);
		event.registerGlobalGroupPermissions(PermissionsAPI.GROUP_DEFAULT, "ForgeEssentials.allowedit.rightclick", false);
		
		event.registerGlobalGroupPermissions(PermissionsAPI.GROUP_ZONE_ADMINS, "ForgeEssentials.allowedit.leftclick", true);
		event.registerGlobalGroupPermissions(PermissionsAPI.GROUP_ZONE_ADMINS, "ForgeEssentials.allowedit.rightclick", true);
		
		event.registerGlobalGroupPermissions(PermissionsAPI.GROUP_OWNERS, "ForgeEssentials.allowedit.leftclick", true);
		event.registerGlobalGroupPermissions(PermissionsAPI.GROUP_OWNERS, "ForgeEssentials.allowedit.rightclick", true);
	}
}