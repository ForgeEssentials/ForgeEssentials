package com.ForgeEssentials.permissions;

import com.ForgeEssentials.api.permissions.IPermissionsRegister;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class ModulePermissions implements IFEModule, IPermissionsRegister
{
	public static PermissionsConfig		config;
	public static PermissionsHandler	pHandler;
	public static ZoneManager			zManager;
	public static boolean				permsVerbose	= false;
	private FEPermissionRegisterEvent	permEvent;

	@PreInit
	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Permissions module is enabled. Loading...");
		zManager = new ZoneManager();
		ZoneManager.GLOBAL = new Zone("__GLOBAL__");
	}

	@Init
	public void load(FMLInitializationEvent e)
	{
		OutputHandler.SOP("Starting permissions registration period.");
		
		PermissionsAPI.registerPermissionsRegistrar(this);
		
		permEvent = new FEPermissionRegisterEvent();
		pHandler = new PermissionsHandler();
		
		MinecraftForge.EVENT_BUS.register(pHandler);
	}

	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{
		OutputHandler.SOP("Ending permissions registration period.");
		permEvent.endPermissionRegistration(this);
		
		for (IPermissionsRegister register : PermissionsAPI.registers)
			register.registerPermissions(permEvent);
		
		PermissionsAPI.registers = null;
		
		config = new PermissionsConfig();
		// TODO Auto-generated method stub

	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandZone());
		e.registerServerCommand(new CommandFEPerm());
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void registerPermissions(FEPermissionRegisterEvent event)
	{
		event.registerGlobalPermission("ForgeEssentials.permissions.zone.list", true);
		event.registerGlobalPermission("ForgeEssentials.permissions.zone.define", true);
		event.registerGlobalPermission("ForgeEssentials.permissions.zone.remove", true);
		event.registerGlobalPermission("ForgeEssentials.permissions.zone.redefine", true);
		event.registerGlobalPermission("ForgeEssentials.permissions.zone.setparent", true);
	}

}
