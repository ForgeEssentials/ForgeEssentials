package com.forgeessentials.auth;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.auth.lists.CommandVIP;
import com.forgeessentials.auth.lists.CommandWhiteList;
import com.forgeessentials.auth.lists.PlayerTracker;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Config;
import com.forgeessentials.core.moduleLauncher.FEModule.Init;
import com.forgeessentials.core.moduleLauncher.FEModule.PreInit;
import com.forgeessentials.core.moduleLauncher.FEModule.ServerInit;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModulePreInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.tasks.TaskRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashSet;

@FEModule(name = "AuthLogin", parentMod = ForgeEssentials.class, configClass = AuthConfig.class)
public class ModuleAuth {
    @Config
    public static AuthConfig config;

    public static boolean forceEnabled;
    public static boolean checkVanillaAuthStatus;

    public static boolean allowOfflineReg;
    public static boolean canMoveWithoutLogin;

    public static VanillaServiceChecker vanillaCheck;
    private static EncryptionHelper pwdEnc;

    private static LoginHandler loginHandler;
    private static AuthEventHandler eventHandler;

    public static HashSet<String> unLogged = new HashSet<String>();
    public static HashSet<String> unRegistered = new HashSet<String>();

    public static String salt = EncryptionHelper.generateSalt();

    public static int checkInterval;

    private static boolean oldEnabled = false;

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
        eventHandler = new AuthEventHandler();

        loginHandler = new LoginHandler();
        GameRegistry.registerPlayerTracker(loginHandler);
        GameRegistry.registerPlayerTracker(new PlayerTracker());

    }

    @ServerInit
    public void serverStarting(FEModuleServerInitEvent e)
    {
        e.registerServerCommand(new CommandAuth());
        e.registerServerCommand(new CommandWhiteList());
        e.registerServerCommand(new CommandVIP());

        if (checkVanillaAuthStatus && !forceEnabled)
        {
            vanillaCheck = new VanillaServiceChecker();
            TaskRegistry.registerRecurringTask(vanillaCheck, 0, checkInterval, 0, 0, 0, checkInterval, 0, 0);
        }

        onStatusChange();

        APIRegistry.permReg.registerPermissionLevel("fe.auth.admin", RegGroup.OWNERS);
        APIRegistry.permReg.registerPermissionLevel("fe.auth", RegGroup.GUESTS);
        APIRegistry.permReg.registerPermissionLevel("fe.auth.vip", null);
        APIRegistry.permReg.registerPermissionLevel("fe.auth.whitelist", RegGroup.GUESTS);
    }

    public static boolean vanillaMode()
    {
        return FMLCommonHandler.instance().getSidedDelegate().getServer().isServerInOnlineMode();
    }

    public static boolean isEnabled()
    {
        if (forceEnabled)
        {
            return true;
        }
        else if (checkVanillaAuthStatus && !vanillaMode())
        {
            return true;
        }

        return false;
    }

    public static void onStatusChange()
    {
        boolean change = oldEnabled != isEnabled();
        oldEnabled = isEnabled();

        if (!change)
        {
            return;
        }

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
