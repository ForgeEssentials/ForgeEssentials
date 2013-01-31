package com.ForgeEssentials.auth;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.FEModule.Config;
import com.ForgeEssentials.api.modules.FEModule.Init;
import com.ForgeEssentials.api.modules.FEModule.ModuleDir;
import com.ForgeEssentials.api.modules.FEModule.PreInit;
import com.ForgeEssentials.api.modules.FEModule.ServerInit;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.backup.BackupConfig;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;

import cpw.mods.fml.common.FMLCommonHandler;

@FEModule(name = "AuthLogin", parentMod = ForgeEssentials.class, configClass = AuthConfig.class)
public class ModuleAuth
{
	@Config
	public static BackupConfig	config;

	public static boolean forceEnabled;

	@PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
	}

	@Init
	public void load(FEModuleInitEvent e)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
	}

	@ForgeSubscribe
	public void registerPermissions(PermissionRegistrationEvent event)
	{
	}
}
