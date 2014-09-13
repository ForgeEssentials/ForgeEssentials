package com.forgeessentials.auth;

import java.util.HashSet;
import java.util.UUID;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.auth.lists.CommandVIP;
import com.forgeessentials.auth.lists.CommandWhiteList;
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

@FEModule(name = "AuthLogin", parentMod = ForgeEssentials.class, configClass = AuthConfig.class)
public class ModuleAuth {
    @Config
    public static AuthConfig config;

    public static boolean forceEnabled;
    public static boolean checkVanillaAuthStatus;

    public static boolean allowOfflineReg;
    public static boolean canMoveWithoutLogin;

    public static VanillaServiceChecker vanillaCheck;
    public static HashSet<UUID> unLogged = new HashSet<UUID>();
    public static HashSet<UUID> unRegistered = new HashSet<UUID>();
    public static String salt = EncryptionHelper.generateSalt();
    public static int checkInterval;
    private static EncryptionHelper pwdEnc;
    private static AuthEventHandler handler;
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
        handler = new AuthEventHandler();
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
        
        PermissionsManager.registerPermission("fe.auth.admin", RegisteredPermValue.OP);
        PermissionsManager.registerPermission("fe.auth", RegisteredPermValue.TRUE);
        PermissionsManager.registerPermission("fe.auth.vip", null);
        PermissionsManager.registerPermission("fe.auth.whitelist", RegisteredPermValue.TRUE);
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
            MinecraftForge.EVENT_BUS.register(handler);
            FMLCommonHandler.instance().bus().register(handler);
        }
        else
        {
            MinecraftForge.EVENT_BUS.unregister(handler);
            FMLCommonHandler.instance().bus().unregister(handler);
        }
    }

    public static String encrypt(String str)
    {
        return pwdEnc.sha1(str);
    }
}
