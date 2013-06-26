package com.ForgeEssentials.auth;

import java.util.HashSet;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.auth.lists.PlayerTracker;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Config;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Init;
import com.ForgeEssentials.core.moduleLauncher.FEModule.PreInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerInit;
import com.ForgeEssentials.util.events.modules.FEModuleInitEvent;
import com.ForgeEssentials.util.events.modules.FEModulePreInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerInitEvent;
import com.ForgeEssentials.util.tasks.TaskRegistry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;

@FEModule(name = "AuthLogin", parentMod = ForgeEssentials.class, configClass = AuthConfig.class)
public class ModuleAuth
{
	@Config
	public static AuthConfig			config;

	public static boolean				forceEnabled;
	public static boolean				checkVanillaAuthStatus;

	public static boolean				allowOfflineReg;

	public static VanillaServiceChecker	vanillaCheck;
	private static EncryptionHelper		pwdEnc;

	private static LoginHandler			loginHandler;
	private static EventHandler			eventHandler;

	public static HashSet<String>		unLogged		= new HashSet<String>();
	public static HashSet<String>		unRegistered	= new HashSet<String>();

	public static String				salt			= EncryptionHelper.generateSalt();

	public static int					checkInterval;

	private static boolean				oldEnabled		= false;

	@PreInit
	public void preInit(FEModulePreInitEvent e)
	{
		// No Auth Module on client
		if (e.getFMLEvent().getSide().isClient())
		{
			e.getModuleContainer().isLoadable = false;
		}
	}

	@Init
	public void load(FEModuleInitEvent e)
	{
		pwdEnc = new EncryptionHelper();
		eventHandler = new EventHandler();

		loginHandler = new LoginHandler();
		GameRegistry.registerPlayerTracker(loginHandler);
		PlayerTracker.whitelist = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().isWhiteListEnabled();
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandAuth());

		if (checkVanillaAuthStatus && !forceEnabled)
		{
			vanillaCheck = new VanillaServiceChecker();
			TaskRegistry.registerRecurringTask(vanillaCheck, 0, checkInterval, 0, 0, 0, checkInterval, 0, 0);
		}

		onStatusChange();
	}

	@PermRegister
	public static void regierPerms(IPermRegisterEvent event)
	{
		event.registerPermissionLevel("ForgeEssentials.ModuleAuth.admin", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.ModuleAuth", RegGroup.GUESTS);
	}

	public static boolean vanillaMode()
	{
		return FMLCommonHandler.instance().getSidedDelegate().getServer().isServerInOnlineMode();
	}

	public static boolean isEnabled()
	{
		if (forceEnabled)
			return true;
		else if (checkVanillaAuthStatus && !vanillaMode())
			return true;

		return false;
	}

	public static void onStatusChange()
	{
		boolean change = oldEnabled != isEnabled();
		oldEnabled = isEnabled();

		if (!change)
			return;

		if (isEnabled())
		{
			MinecraftForge.EVENT_BUS.register(eventHandler);
		}
		else
		{
			MinecraftForge.EVENT_BUS.unregister(eventHandler);
		}
	}

	public static String encrypt(String str)
	{
		return pwdEnc.sha1(str);
	}
}
