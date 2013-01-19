package com.ForgeEssentials.backup;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.moduleLauncher.FEModule.Config;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Init;
import com.ForgeEssentials.core.moduleLauncher.FEModule.PreInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerInit;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;
import com.ForgeEssentials.permission.RegGroup;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class ModuleBackup
{
	@Config
	public static BackupConfig config;
	
	public static BackupThread thread;

	@PreInit
	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Backup module is enabled. Loading...");
	}

	@Init
	public void load(FMLInitializationEvent e)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@ServerInit
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandBackup());
	}

	@ForgeSubscribe
	public void registerPermissions(PermissionRegistrationEvent event)
	{
		event.registerPerm(this, RegGroup.OWNERS, "ForgeEssentials.backup", true);
	}
}