package com.forgeessentials.auth;

import java.util.HashSet;
import java.util.TimerTask;
import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet0Handshake;
import com.forgeessentials.commons.network.packets.Packet6AuthLogin;
import com.forgeessentials.commons.network.packets.Packet7Remote;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Preconditions;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleCommonSetupEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.mojang.brigadier.Command;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

@FEModule(name = "AuthLogin", parentMod = ForgeEssentials.class, defaultModule = false)
public class ModuleAuth
{

    public static final String CONFIG_CATEGORY = "Auth";
    public static final String CONFIG_CATEGORY_LISTS = "Authlists";
    protected static final String SCRIPT_KEY_SUCCESS = "AuthLoginSuccess";
    protected static final String SCRIPT_KEY_FAILURE = "AuthLoginFailure";

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
        if (FMLEnvironment.dist.isClient())
            return false;
        return true;
    }

    @SubscribeEvent
    public void load(FEModuleCommonSetupEvent e)
    {
        FECommandManager.registerCommand(new CommandAuth());
        FECommandManager.registerCommand(new CommandVIP());
        NetworkUtils.registerServerToClient(6, Packet6AuthLogin.class, Packet6AuthLogin::decode);

    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerStartingEvent e)
    {
        APIRegistry.perms.registerPermission("fe.auth.admin", DefaultPermissionLevel.OP, "Administer the auth module");
        APIRegistry.perms.registerPermission("fe.auth", DefaultPermissionLevel.ALL, "Auth module command");
        APIRegistry.perms.registerPermission("fe.auth.vip", null, "Player VIP status");
        if (isEnabled())
        {
            handler = new AuthEventHandler();
            handler.enable(true);
        }
        APIRegistry.scripts.addScriptType(SCRIPT_KEY_SUCCESS);
        APIRegistry.scripts.addScriptType(SCRIPT_KEY_FAILURE);
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
        ServerLifecycleHooks.getCurrentServer().setUsesAuthentication(isOnline);
        if (lastEnabled == isEnabled())
            return;

        if (isEnabled())
        {
            handler.enable(true);
        }
        else
        {
            try
            {
                handler.enable(false);
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

    public static boolean isAuthenticated(PlayerEntity player)
    {
        if (player == null) {
            return true;
        }
        return isAuthenticated(player.getUUID());
    }

    public static boolean isAllowedMethod(IMessage msg) {
        return msg instanceof Packet6AuthLogin || msg instanceof Packet0Handshake || msg instanceof Packet7Remote;
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
    public static boolean isGuestCommand(Command command)
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
    
    static ForgeConfigSpec.ConfigValue<String> FEalgorithm;
    static ForgeConfigSpec.BooleanValue FEcanMoveWithoutLogin;
    static ForgeConfigSpec.BooleanValue FEallowOfflineRegistration;
    static ForgeConfigSpec.BooleanValue FEforceEnabled;
    static ForgeConfigSpec.ConfigValue<String> FEsalt;
    static ForgeConfigSpec.BooleanValue FEallowAutoLogin;
    static ForgeConfigSpec.BooleanValue FEcheckVanillaAuthStatus;
    static ForgeConfigSpec.IntValue FEauthCheckerInterval;
    
    static ForgeConfigSpec.IntValue FEreservedSlots;
    static ForgeConfigSpec.IntValue FEvipSlots;
    
    static ForgeConfigSpec.ConfigValue<String> FEplayerBannedMessage;
    static ForgeConfigSpec.ConfigValue<String> FEnonVipKickMessage;
    public static void load(ForgeConfigSpec.Builder BUILDER)
    {
    	BUILDER.comment("AuthModule configuration").push(CONFIG_CATEGORY);
    	FEalgorithm = BUILDER.comment(CFG_DESC_encryption).define("encryptionAlgorithm", "SHA1");
    	FEcanMoveWithoutLogin = BUILDER.comment(CFG_DESC_canMoveWithoutLogin).define("canMoveWithoutLogin", false);
    	FEallowOfflineRegistration = BUILDER.comment(CFG_DESC_allowOfflineReg).define("allowOfflineReg", false);
    	FEforceEnabled = BUILDER.comment(CFG_DESC_forceEnable).define("forceEnable", false);
    	FEsalt = BUILDER.comment(CFG_DESC_salt).define("salt", EncryptionHelper.generateSalt());
    	FEallowAutoLogin = BUILDER.comment(CFG_DESC_autologin).define("allowAutoLogin", false);
    	FEcheckVanillaAuthStatus = BUILDER.comment(CFG_DESC_autoEnable).define("autoEnable", false);
    	FEauthCheckerInterval = BUILDER.comment(CFG_DESC_checkInterval).defineInRange("checkInterval", 10, 0, Integer.MAX_VALUE);
    	BUILDER.pop();
    	
    	BUILDER.comment(CFG_DESC_authlists).push(CONFIG_CATEGORY_LISTS);
    	FEreservedSlots = BUILDER.comment(CFG_DESC_offset).defineInRange("offset", 0, 0, Integer.MAX_VALUE);
    	FEvipSlots = BUILDER.comment("Amount of slots reserved for VIP players.").defineInRange("vipslots", 0, 0, Integer.MAX_VALUE);
    	BUILDER.pop();
        
    	BUILDER.comment(CFG_DESC_kickMsg).push(CONFIG_CATEGORY_LISTS+"_kickmsg");
    	FEplayerBannedMessage = BUILDER.define("bannedmsg", "You have been banned from this server.");
    	FEnonVipKickMessage = BUILDER.define("notVIPmsg", "This server is full, and you are not a VIP.");
    	BUILDER.pop();
    }

	public static void bakeConfig(boolean reload) {
		EncryptionHelper.algorithm = FEalgorithm.get();
		canMoveWithoutLogin = FEcanMoveWithoutLogin.get();
		allowOfflineRegistration = FEallowOfflineRegistration.get();
		forceEnabled = FEforceEnabled.get();
		PasswordManager.setSalt(FEsalt.get());
		
        AuthEventHandler.reservedSlots = FEreservedSlots.get();
        AuthEventHandler.vipSlots = FEvipSlots.get();
        AuthEventHandler.playerBannedMessage = FEplayerBannedMessage.get();
        AuthEventHandler.nonVipKickMessage = FEnonVipKickMessage.get();
        
        allowAutoLogin = FEallowAutoLogin.get();
        checkVanillaAuthStatus = FEcheckVanillaAuthStatus.get();
        int authCheckerInterval = FEauthCheckerInterval.get();
        
        if (checkVanillaAuthStatus && !forceEnabled)
            TaskRegistry.scheduleRepeated(mojangServiceChecker, authCheckerInterval * 60 * 1000);
        else
            TaskRegistry.remove(mojangServiceChecker);
	}

}
