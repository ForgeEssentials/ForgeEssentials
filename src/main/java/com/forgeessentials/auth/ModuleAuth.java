package com.forgeessentials.auth;

import java.util.HashSet;
import java.util.TimerTask;
import java.util.UUID;

import net.minecraft.command.CommandHelp;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet6AuthLogin;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Preconditions;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@FEModule(name = "AuthLogin", parentMod = ForgeEssentials.class, defaultModule = false)
public class ModuleAuth extends ConfigLoaderBase
{

    private static final String CONFIG_CATEGORY = "Auth";
    private static final String CONFIG_CATEGORY_LISTS = "Authlists";

    static boolean forceEnabled;
    static boolean allowOfflineRegistration;
    static boolean canMoveWithoutLogin;
    static boolean checkVanillaAuthStatus;
    static boolean allowAutoLogin;

    private static HashSet<UUID> authenticatedUsers = new HashSet<>();

    private static AuthEventHandler handler;

    private static boolean isOnline = true;

    private static TimerTask mojangServiceChecker = new TimerTask() {
        @Override
        public void run()
        {
            checkMojangStatus();
        }

    };

    @Preconditions
    public boolean preInit()
    {
        if (FMLCommonHandler.instance().getSide().isClient())
            return false;
        return true;
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        FECommandManager.registerCommand(new CommandAuth());
        FECommandManager.registerCommand(new CommandVIP());
        NetworkUtils.registerMessage(new AuthNetHandler(), Packet6AuthLogin.class, 6, Side.SERVER);
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        APIRegistry.perms.registerPermission("fe.auth.admin", PermissionLevel.OP);
        APIRegistry.perms.registerPermission("fe.auth", PermissionLevel.TRUE);
        APIRegistry.perms.registerPermission("fe.auth.vip", null);
        if (isEnabled())
        {
            handler = new AuthEventHandler();
        }
    }

    public static boolean isEnabled()
    {
        return forceEnabled || checkVanillaAuthStatus && !ServerUtil.isOnlineMode();
    }

    public static void checkMojangStatus()
    {
        boolean lastEnabled = isEnabled();
        boolean lastOnline = isOnline;
        isOnline = ServerUtil.getMojangServerStatus();
        if (lastOnline == isOnline)
            return;

        FMLCommonHandler.instance().getSidedDelegate().getServer().setOnlineMode(isOnline);
        if (lastEnabled == isEnabled())
            return;

        if (isEnabled())
        {
            MinecraftForge.EVENT_BUS.register(handler);
            FMLCommonHandler.instance().bus().register(handler);
        }
        else
        {
            try
            {
                MinecraftForge.EVENT_BUS.unregister(handler);
                FMLCommonHandler.instance().bus().unregister(handler);
            }
            catch (NullPointerException e)
            {
                /* catch forge bug */
            }
        }
    }

    /**
     * Checks, if a player is registered
     * 
     * @param user
     * @return
     */
    public static boolean isRegistered(UUID user)
    {
        return PasswordManager.hasPassword(user);
    }

    public static boolean isAuthenticated(UUID player)
    {
        return authenticatedUsers.contains(player);
    }

    public static boolean isAuthenticated(EntityPlayer player)
    {
        return isAuthenticated(player.getPersistentID());
    }

    public static void authenticate(UUID player)
    {
        authenticatedUsers.add(player);
    }

    public static void deauthenticate(UUID player)
    {
        authenticatedUsers.remove(player);
    }

    /**
     * Check, if unauthenticated users are allowed to use this command
     * 
     * @param command
     * @return
     */
    public static boolean isGuestCommand(ICommand command)
    {
        return command instanceof CommandAuth || //
                command instanceof CommandHelp;
    }

    private static final String CFG_DESC_forceEnable = "Forces the authentication server to be loaded regardless of Minecraft auth services";
    private static final String CFG_DESC_autoEnable = "Enable the authentication service automatically if Minecraft auth services are not available";
    private static final String CFG_DESC_allowOfflineReg = "Allows people to register usernames while server is offline. Don't allow this for primarily Online servers.";
    private static final String CFG_DESC_salt = "The salt to be used when hashing passwords";
    private static final String CFG_DESC_checkInterval = "Interval to check Vanilla Auth service in minutes.";
    private static final String CFG_DESC_canMoveWithoutLogin = "Allow players not registered/not logged in with the authentication service to move in the world.";
    private static final String CFG_DESC_kickMsg = "Kick messages for banned/unwhitelisted players or when the server is full (not counting VIP slots";
    private static final String CFG_DESC_authlists = "Alternative VIP/max players implementation. Make sure vipslots and offset added together is less than the amount of players specified in server.properties.";
    private static final String CFG_DESC_offset = "If you need to be able to have less than the amount of players specified in server.properties logged into your server, use this.";
    private static final String CFG_DESC_autologin = "Allow players with the FEClient and the correct keys to automatically identify themselves with the auth engine.";
    private static final String CFG_DESC_encryption = "Encryption standard to use. Note that changing this will invalidate all passwords. Accepts the following: SHA1, SHA-256, MD5";

    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment(CONFIG_CATEGORY, "AuthModule configuration");
        EncryptionHelper.algorithm = config.get(CONFIG_CATEGORY, "encryptionAlgorithm", "SHA1", CFG_DESC_encryption).getString();
        canMoveWithoutLogin = config.get(CONFIG_CATEGORY, "canMoveWithoutLogin", false, CFG_DESC_canMoveWithoutLogin).getBoolean(false);
        allowOfflineRegistration = config.get(CONFIG_CATEGORY, "allowOfflineReg", false, CFG_DESC_allowOfflineReg).getBoolean(false);
        forceEnabled = config.get(CONFIG_CATEGORY, "forceEnable", false, CFG_DESC_forceEnable).getBoolean(false);
        PasswordManager.setSalt(config.get(CONFIG_CATEGORY, "salt", EncryptionHelper.generateSalt(), CFG_DESC_salt).getString());

        config.addCustomCategoryComment(CONFIG_CATEGORY_LISTS, CFG_DESC_authlists);
        AuthEventHandler.reservedSlots = config.get(CONFIG_CATEGORY_LISTS, "offset", 0, CFG_DESC_offset).getInt();
        AuthEventHandler.vipSlots = config.get(CONFIG_CATEGORY_LISTS, "vipslots", 0, "Amount of slots reserved for VIP players.").getInt();

        config.addCustomCategoryComment(CONFIG_CATEGORY_LISTS + ".kickmsg", CFG_DESC_kickMsg);
        AuthEventHandler.playerBannedMessage = config.get(CONFIG_CATEGORY_LISTS + ".kick", "bannedmsg", "You have been banned from this server.").getString();
        AuthEventHandler.nonVipKickMessage = config.get(CONFIG_CATEGORY_LISTS + ".kick", "notVIPmsg", "This server is full, and you are not a VIP.")
                .getString();
        allowAutoLogin = config.get(CONFIG_CATEGORY, "allowAutoLogin", true, CFG_DESC_autologin).getBoolean();

        checkVanillaAuthStatus = config.get(CONFIG_CATEGORY, "autoEnable", false, CFG_DESC_autoEnable).getBoolean(false);
        int authCheckerInterval = config.get(CONFIG_CATEGORY, "checkInterval", 10, CFG_DESC_checkInterval).getInt();
        if (checkVanillaAuthStatus && !forceEnabled)
            TaskRegistry.scheduleRepeated(mojangServiceChecker, authCheckerInterval * 60 * 1000);
        else
            TaskRegistry.remove(mojangServiceChecker);
    }

}
