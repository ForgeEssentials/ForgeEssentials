package com.ForgeEssentials.auth;

import java.util.HashSet;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.FEModule.Config;
import com.ForgeEssentials.api.modules.FEModule.Init;
import com.ForgeEssentials.api.modules.FEModule.PreInit;
import com.ForgeEssentials.api.modules.FEModule.ServerInit;
import com.ForgeEssentials.api.modules.FEModule.ServerStop;
import com.ForgeEssentials.api.modules.ModuleDisableException;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.auth.commands.CommandLogin;
import com.ForgeEssentials.auth.commands.CommandRegister;
import com.ForgeEssentials.core.ForgeEssentials;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@FEModule(name = "AuthLogin", parentMod = ForgeEssentials.class, configClass = AuthConfig.class)
public class ModuleAuth
{
	@Config
	public static AuthConfig			config;

	public static boolean				forceEnabled;
	public static boolean				checkVanillaAuthStatus;

	public static boolean				enabled;

	public static boolean				allowOfflineReg;

	public static VanillaServiceChecker	vanillaCheck;
	public static EncryptionHelper		pwdEnc;

	private static LoginHandler			loginHandler;
	private static EventHandler			eventHandler;

	public static HashSet<String>		unLogged		= new HashSet<String>();
	public static HashSet<String>		unRegistered	= new HashSet<String>();

	@PreInit
	public void preInit(FEModulePreInitEvent e)
	{
		// No Auth Module on client
		if (e.getFMLEvent().getSide().isClient())
			throw new ModuleDisableException("Cannot run on client");
	}

	@Init
	public void load(FEModuleInitEvent e)
	{
		pwdEnc = new EncryptionHelper();
		eventHandler = new EventHandler();
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandLogin());
		e.registerServerCommand(new CommandRegister());

		if (checkVanillaAuthStatus && !forceEnabled)
		{
			vanillaCheck = new VanillaServiceChecker();
			TickRegistry.registerScheduledTickHandler(vanillaCheck, Side.SERVER);
		}

		loginHandler = new LoginHandler();
		GameRegistry.registerPlayerTracker(loginHandler);

		MinecraftForge.EVENT_BUS.register(eventHandler);
	}

	@ServerStop
	public void serverStop(FEModuleServerStopEvent e)
	{
		if (loginHandler != null)
		{
			loginHandler = null;
		}

		MinecraftForge.EVENT_BUS.unregister(eventHandler);
	}

	public static boolean vanillaMode()
	{
		return FMLCommonHandler.instance().getSidedDelegate().getServer().isServerInOnlineMode();
	}
}
