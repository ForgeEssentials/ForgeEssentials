package com.forgeessentials.auth;

import java.util.HashSet;
import java.util.UUID;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.auth.lists.CommandVIP;
import com.forgeessentials.auth.lists.CommandWhiteList;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Config;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.tasks.TaskRegistry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "AuthLogin", parentMod = ForgeEssentials.class, configClass = AuthConfig.class)
public class ModuleAuth {
    @Config
    public static AuthConfig config;

    public static boolean forceEnabled;
    public static boolean checkVanillaAuthStatus;

    public static boolean allowOfflineReg;
    public static boolean canMoveWithoutLogin;

    public static VanillaServiceChecker vanillaCheck;
    public static HashSet<UUID> hasSession = new HashSet<>();
    public static String salt = EncryptionHelper.generateSalt();
    public static int checkInterval;
    private static EncryptionHelper pwdEnc;
    private static AuthEventHandler handler;
    private static boolean oldEnabled = false;

    @SubscribeEvent
    public void preInit(FEModulePreInitEvent e)
    {
        // No Auth Module on client
        if (e.getFMLEvent().getSide().isClient())
        {
            ModuleLauncher.instance.unregister("AuthLogin");
        }
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        pwdEnc = new EncryptionHelper();
        handler = new AuthEventHandler();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        e.registerServerCommand(new CommandAuth());

        if (AuthEventHandler.whitelist)
        {
            e.registerServerCommand(new CommandWhiteList());
            e.registerServerCommand(new CommandVIP());
        }

        if (checkVanillaAuthStatus && !forceEnabled)
        {
            vanillaCheck = new VanillaServiceChecker();
            TaskRegistry.registerRecurringTask(vanillaCheck, 0, checkInterval, 0, 0, 0, checkInterval, 0, 0);
        }

        onStatusChange();
        
        APIRegistry.perms.registerPermission("fe.auth.admin", RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission("fe.auth", RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission("fe.auth.vip", null);
        APIRegistry.perms.registerPermission("fe.auth.whitelist", RegisteredPermValue.TRUE);
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
