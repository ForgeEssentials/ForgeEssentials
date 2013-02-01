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
import com.ForgeEssentials.api.modules.FEModule.ServerStop;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.backup.BackupConfig;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@FEModule(name = "AuthLogin", parentMod = ForgeEssentials.class, configClass = AuthConfig.class)
public class ModuleAuth
{
	@Config
	public static AuthConfig	config;

	public static boolean forceEnabled;
	public static boolean checkVanillaAuthStatus;
	
	public static boolean enabled;
	
	LoginHandler handler;

	@Init
	public void load(FEModuleInitEvent e)
	{
		// No Auth Plugin on client
		if (e.getFMLEvent().getSide().isClient())
		{
			enabled = false;
			checkVanillaAuthStatus = false;
		}
		
		if (checkVanillaAuthStatus)
			TickRegistry.registerScheduledTickHandler(new VanillaServiceChecker(), Side.SERVER);
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		if (enabled)
		{
			handler = new LoginHandler();
			GameRegistry.registerPlayerTracker(handler);
			MinecraftForge.EVENT_BUS.register(handler);
		}
	}
	
	@ServerStop
	public void serverStop(FEModuleServerStopEvent e)
	{
		if (handler != null)
		{
			MinecraftForge.EVENT_BUS.unregister(handler);
			handler = null;
		}
	}
}