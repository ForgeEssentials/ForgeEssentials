package com.forgeessentials.teleport;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.teleport.portal.CommandPortal;
import com.forgeessentials.teleport.portal.PortalManager;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;

@FEModule(name = "Teleport", parentMod = ForgeEssentials.class)
public class TeleportModule extends ConfigLoaderBase
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

    public static final String PERM_JUMP = "fe.teleport.jump";
    public static final String PERM_JUMP_TOOL = PERM_JUMP + ".tool";

    private PortalManager portalManager;

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);

        portalManager = new PortalManager();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        portalManager.load();

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

        APIRegistry.perms.registerPermissionProperty(PERM_TPA_TIMEOUT, "20", "Amount of sec a user has to accept a TPA request");

        APIRegistry.perms.registerPermission(PERM_BACK_ONDEATH, PermissionLevel.TRUE, "Allow returning to the last death location with back-command");
        APIRegistry.perms
                .registerPermission(PERM_BACK_ONTP, PermissionLevel.TRUE, "Allow returning to the last location before teleport with back-command");
        APIRegistry.perms.registerPermission(PERM_BED_OTHERS, PermissionLevel.OP, "Allow teleporting to other player's bed location");

        APIRegistry.perms.registerPermission(PERM_HOME, PermissionLevel.TRUE, "Allow usage of /home");
        APIRegistry.perms.registerPermission(PERM_HOME_SET, PermissionLevel.TRUE, "Allow setting of home location");
        APIRegistry.perms.registerPermission(PERM_HOME_OTHER, PermissionLevel.OP, "Allow setting other players home location");

        APIRegistry.perms.registerPermission(PERM_SPAWN_OTHERS, PermissionLevel.OP, "Allow setting other player's spawn");
        APIRegistry.perms.registerPermission(PERM_TOP_OTHERS, PermissionLevel.OP);
        APIRegistry.perms.registerPermission(PERM_TPA_SENDREQUEST, PermissionLevel.TRUE, "Allow sending teleport-to requests");
        APIRegistry.perms.registerPermission(PERM_TPAHERE_SENDREQUEST, PermissionLevel.TRUE, "Allow sending teleport-here requests");
        APIRegistry.perms.registerPermission(PERM_WARP_ADMIN, PermissionLevel.OP);

        APIRegistry.perms.registerPermission(PERM_JUMP_TOOL, PermissionLevel.OP, "Allow jumping with a tool (default compass)");
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        String portalBlockId = config.get(Configuration.CATEGORY_GENERAL, "portalBlock", "minecraft:glass_pane", "Name of the block to use as material for new portals.\n"
                + "Does not override vanilla nether/end portals.\nSetting this to 'minecraft:portal' is currently not supported.").getString();
        PortalManager.portalBlock = GameData.getBlockRegistry().getObject(new ResourceLocation(portalBlockId));
    }
}
