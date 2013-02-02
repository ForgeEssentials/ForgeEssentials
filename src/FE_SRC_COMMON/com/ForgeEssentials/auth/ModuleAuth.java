package com.ForgeEssentials.auth;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.FEModule.Config;
import com.ForgeEssentials.api.modules.FEModule.Init;
import com.ForgeEssentials.api.modules.FEModule.ServerInit;
import com.ForgeEssentials.api.modules.FEModule.ServerStop;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.auth.commands.CommandLogin;
import com.ForgeEssentials.auth.commands.CommandRegister;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;

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
	
	public static LoginHandler handler;
	public static VanillaServiceChecker vanillaCheck;
	public static PasswordEncryptionService pwdEnc;
	
	private static boolean vanillaOnlineMode;

	public static boolean allowOfflineReg;

	@Init
	public void load(FEModuleInitEvent e)
	{
		// No Auth Plugin on client
		if (e.getFMLEvent().getSide().isClient())
		{
			enabled = false;
			checkVanillaAuthStatus = false;
		}
		
		pwdEnc = new PasswordEncryptionService();
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandLogin());
		e.registerServerCommand(new CommandRegister());
		
		if (checkVanillaAuthStatus)
		{
			vanillaOnlineMode = e.getServer().isServerInOnlineMode();
			vanillaCheck = new VanillaServiceChecker();
			TickRegistry.registerScheduledTickHandler(vanillaCheck, Side.SERVER);
		}
		
		if (e.getFMLEvent().getSide().isServer())
		{
			handler = new LoginHandler();
			GameRegistry.registerPlayerTracker(handler);
			MinecraftForge.EVENT_BUS.register(handler);
		}
	}
	
	public static boolean getVanillaOnlineMode() 
	{
		return vanillaOnlineMode;
	}
	
	public static void FEAuth(Boolean onlineMode)
	{
		if(onlineMode)
		{
			boolean bln = ModuleAuth.getVanillaOnlineMode();
			
			if(bln != FMLCommonHandler.instance().getMinecraftServerInstance().isServerInOnlineMode())
			{
				if(bln) OutputHandler.warning("Server set to Online mode!");
				else 	OutputHandler.warning("Server set to Offline mode!");	
			}
			
			FMLCommonHandler.instance().getMinecraftServerInstance().setOnlineMode(bln);
			
			if(!forceEnabled)
			{
				OutputHandler.fine("FEauth disabled.");
				enabled = false;
			}
			else
			{
				OutputHandler.fine("FEauth remains enabled due to forceEnabled in config.");
				enabled = true;
			}
		}
		else
		{
			if(enabled)
			{
				OutputHandler.fine("FEauth remains enabled.");
			}
			else
			{
				OutputHandler.fine("FEauth enabled.");
				enabled = true;
				
				if(FMLCommonHandler.instance().getMinecraftServerInstance().isServerInOnlineMode())
				{
					FMLCommonHandler.instance().getMinecraftServerInstance().setOnlineMode(false);
					OutputHandler.warning("Server set to Offline mode!");
				}
			}
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