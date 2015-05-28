package com.forgeessentials.teleport;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.RespawnHandler;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.teleport.portal.CommandPortal;
import com.forgeessentials.teleport.portal.PortalManager;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "Teleport", parentMod = ForgeEssentials.class)
public class TeleportModule
{

    public static final String PERM_TP = "fe.teleport.tp";
    public static final String PERM_TP_OTHERS = "fe.teleport.tp.others";

    public static final String PERM_TPPOS = "fe.teleport.tppos";

    public static final String PERM_TPA = "fe.teleport.tpa";
    public static final String PERM_TPA_SENDREQUEST = "fe.teleport.tpa.sendrequest";
    public static final String PERM_TPA_TIMEOUT = "fe.teleport.tpa.timeout";

    public static final String PERM_TOP = "fe.teleport.top";
    public static final String PERM_TOP_OTHERS = "fe.teleport.top.others";

    public static final String PERM_SPAWN = "fe.teleport.spawn";
    public static final String PERM_SPAWN_OTHERS = "fe.teleport.spawn.others";

    public static final String PERM_HOME = "fe.teleport.home";
    public static final String PERM_HOME_SET = PERM_HOME + ".set";
    public static final String PERM_HOME_OTHER = PERM_HOME + ".other";

    public static final String PERM_BED = "fe.teleport.bed";
    public static final String PERM_BED_OTHERS = PERM_BED + ".others";

    public static final String PERM_BACK = "fe.teleport.back";
    public static final String PERM_BACK_ONTP = PERM_BACK + ".ontp";
    public static final String PERM_BACK_ONDEATH = PERM_BACK + ".ondeath";

    public static final String PERM_TPAHERE = "fe.teleport.tpahere";
    public static final String PERM_TPAHERE_SENDREQUEST = "fe.teleport.tpahere.sendrequest";

    public static final String PERM_TPHERE = "fe.teleport.tphere";

    public static final String PERM_WARP = "fe.teleport.warp";
    public static final String PERM_WARP_ADMIN = "fe.teleport.warp.admin";

    private PortalManager portalManager;

    @SuppressWarnings("unused")
    private RespawnHandler respawnHandler;

    static
    {
        FECommandManager.registerCommand(new CommandBack());
        FECommandManager.registerCommand(new CommandBed());
        FECommandManager.registerCommand(new CommandHome());
        FECommandManager.registerCommand(new CommandSpawn());
        FECommandManager.registerCommand(new CommandTp());
        FECommandManager.registerCommand(new CommandTppos());
        FECommandManager.registerCommand(new CommandWarp());
        FECommandManager.registerCommand(new CommandTPA());
        FECommandManager.registerCommand(new CommandPersonalWarp());
        FECommandManager.registerCommand(new CommandTop());
        FECommandManager.registerCommand(new CommandPortal());
        FECommandManager.registerCommand(new CommandSetSpawn());
        FECommandManager.registerCommand(new CommandJump());
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);

        respawnHandler = new RespawnHandler();

        portalManager = new PortalManager();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        portalManager.load();

        APIRegistry.perms.registerPermissionProperty(PERM_TPA_TIMEOUT, "20", "Amount of sec a user has to accept a TPA request");

        APIRegistry.perms.registerPermission(PERM_BACK_ONDEATH, RegisteredPermValue.TRUE, "Allow returning to the last death location with back-command");
        APIRegistry.perms
                .registerPermission(PERM_BACK_ONTP, RegisteredPermValue.TRUE, "Allow returning to the last location before teleport with back-command");
        APIRegistry.perms.registerPermission(PERM_BED_OTHERS, RegisteredPermValue.OP, "Allow teleporting to other player's bed location");

        APIRegistry.perms.registerPermission(PERM_HOME, RegisteredPermValue.TRUE, "Allow usage of /home");
        APIRegistry.perms.registerPermission(PERM_HOME_SET, RegisteredPermValue.TRUE, "Allow setting of home location");
        APIRegistry.perms.registerPermission(PERM_HOME_OTHER, RegisteredPermValue.OP, "Allow setting other players home location");

        APIRegistry.perms.registerPermission(PERM_SPAWN_OTHERS, RegisteredPermValue.OP, "Allow setting other player's spawn");
        APIRegistry.perms.registerPermission(PERM_TOP_OTHERS, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(PERM_TPA_SENDREQUEST, RegisteredPermValue.TRUE, "Allow sending teleport-to requests");
        APIRegistry.perms.registerPermission(PERM_TPAHERE_SENDREQUEST, RegisteredPermValue.TRUE, "Allow sending teleport-here requests");
        APIRegistry.perms.registerPermission(PERM_WARP_ADMIN, RegisteredPermValue.OP);
    }

}
