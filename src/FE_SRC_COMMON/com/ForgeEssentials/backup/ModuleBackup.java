package com.ForgeEssentials.backup;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.RegGroup;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class ModuleBackup implements IFEModule
{
	public static BackupConfig	config;
	public static BackupThread thread;

	public ModuleBackup()
	{

	}

	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Backups module is enabled. Loading...");
		config = new BackupConfig();
	}

	public void load(FMLInitializationEvent e)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{
	}

	@Override
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandBackup());
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
	{
	}

	@ForgeSubscribe
	public void registerPermissions(PermissionRegistrationEvent event)
	{
		event.registerPerm(this, RegGroup.OWNERS, "ForgeEssentials.backup", true);
	}

	@Override
	public void serverStopping(FMLServerStoppingEvent e)
	{
		// TODO Auto-generated method stub

	}
}