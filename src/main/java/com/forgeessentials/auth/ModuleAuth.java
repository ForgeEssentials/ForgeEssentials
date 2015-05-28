package com.forgeessentials.auth;

import java.util.HashSet;
import java.util.UUID;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.auth.lists.CommandVIP;
import com.forgeessentials.auth.lists.CommandWhiteList;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "AuthLogin", parentMod = ForgeEssentials.class)
public class ModuleAuth extends ConfigLoaderBase
{

    private static final String CONFIG_CATEGORY = "Auth";
    private static final String CONFIG_CATEGORY_LISTS = "Authlists";

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
        
        FECommandManager.registerCommand(new CommandAuth());
        if (AuthEventHandler.whitelist)
        {
            FECommandManager.registerCommand(new CommandWhiteList());
            FECommandManager.registerCommand(new CommandVIP());
        }
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        if (checkVanillaAuthStatus && !forceEnabled)
        {
            vanillaCheck = new VanillaServiceChecker();
            TaskRegistry.getInstance().scheduleRepeated(vanillaCheck, checkInterval * 60 * 1000);
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

    private static final String CFG_DESC_forceEnable = "Forces the authentication server to be loaded regardless of Minecraft auth services";
    private static final String CFG_DESC_autoEnable = "Forces the authentication server to be loaded regardless of Minecraft auth services";
    private static final String CFG_DESC_allowOfflineReg = "Allows people to register usernames while server is offline. Don't allow this for primarily Online servers.";
    private static final String CFG_DESC_salt = "The salt to be used when hashing passwords";
    private static final String CFG_DESC_checkInterval = "Interval to check Vanill Auth service in minutes.";
    private static final String CFG_DESC_canMoveWithoutLogin = "Allow players not registered/not logged in with the authentication service to move in the world.";
    private static final String CFG_DESC_kickMsg = "Kick messages for banned/unwhitelisted players or when the server is full (not counting VIP slots";
    private static final String CFG_DESC_authlists = "Alternative ban/whitelist/VIP/max players implementation. Make sure vipslots and offset added together is less than the amount of players specified in server.properties.";
    private static final String CFG_DESC_offset = "If you need to be able to have less than the amount of players specified in server.properties logged into your server, use this.";
    private static final String CFG_DESC_whitelistEnabled = "Enable or disable the ForgeEssentials whitelist. Note that server.properties will be used if this is set to false.";

    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment(CONFIG_CATEGORY, "AuthModule configuration");
        checkVanillaAuthStatus = config.get(CONFIG_CATEGORY, "autoEnable", false, CFG_DESC_autoEnable).getBoolean(false);
        canMoveWithoutLogin = config.get(CONFIG_CATEGORY, "canMoveWithoutLogin", false, CFG_DESC_canMoveWithoutLogin).getBoolean(false);
        allowOfflineReg = config.get(CONFIG_CATEGORY, "allowOfflineReg", false, CFG_DESC_allowOfflineReg).getBoolean(false);
        checkInterval = config.get(CONFIG_CATEGORY, "checkInterval", 10, CFG_DESC_checkInterval).getInt();
        forceEnabled = config.get(CONFIG_CATEGORY, "forceEnable", false, CFG_DESC_forceEnable).getBoolean(false);
        salt = config.get(CONFIG_CATEGORY, "salt", salt, CFG_DESC_salt).getString();

        config.addCustomCategoryComment(CONFIG_CATEGORY_LISTS, CFG_DESC_authlists);
        AuthEventHandler.offset = config.get(CONFIG_CATEGORY_LISTS, "offset", 0, CFG_DESC_offset).getInt();
        AuthEventHandler.vipslots = config.get(CONFIG_CATEGORY_LISTS, "vipslots", 0, "Amount of slots reserved for VIP players.").getInt();
        AuthEventHandler.whitelist = config.get(CONFIG_CATEGORY_LISTS, "whitelistEnabled", false, CFG_DESC_whitelistEnabled).getBoolean(false);

        config.addCustomCategoryComment(CONFIG_CATEGORY_LISTS + ".kickmsg", CFG_DESC_kickMsg);
        AuthEventHandler.banned = config.get(CONFIG_CATEGORY_LISTS + ".kick", "bannedmsg", "You have been banned from this server.").getString();
        AuthEventHandler.notwhitelisted = config.get(CONFIG_CATEGORY_LISTS + ".kick", "unwhitelistedmsg", "You are not whitelisted on this server.")
                .getString();
        AuthEventHandler.notvip = config.get(CONFIG_CATEGORY_LISTS + ".kick", "notVIPmsg", "This server is full, and you are not a VIP.").getString();
    }

    @Override
    public void save(Configuration config)
    {
        config.get(CONFIG_CATEGORY, "allowOfflineReg", false, CFG_DESC_allowOfflineReg).set(allowOfflineReg);
        config.get(CONFIG_CATEGORY, "checkInterval", "", CFG_DESC_checkInterval).set(checkInterval);
        config.get(CONFIG_CATEGORY, "forceEnable", false, CFG_DESC_forceEnable).set(forceEnabled);
        config.get(CONFIG_CATEGORY, "autoEnable", true, CFG_DESC_autoEnable).set(checkVanillaAuthStatus);
        config.get(CONFIG_CATEGORY, "salt", "", CFG_DESC_salt).set(salt);

    }

}
