package com.forgeessentials.teleport;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.registration.FECommandManager;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoaderBase;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.teleport.commands.CommandBack;
import com.forgeessentials.teleport.commands.CommandBed;
import com.forgeessentials.teleport.commands.CommandHome;
import com.forgeessentials.teleport.commands.CommandJump;
import com.forgeessentials.teleport.commands.CommandPersonalWarp;
import com.forgeessentials.teleport.commands.CommandSetSpawn;
import com.forgeessentials.teleport.commands.CommandSpawn;
import com.forgeessentials.teleport.commands.CommandTPA;
import com.forgeessentials.teleport.commands.CommandTop;
import com.forgeessentials.teleport.commands.CommandTp;
import com.forgeessentials.teleport.commands.CommandTppos;
import com.forgeessentials.teleport.commands.CommandWarp;
import com.forgeessentials.teleport.portal.CommandPortal;
import com.forgeessentials.teleport.portal.PortalManager;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

@FEModule(name = "Teleport", parentMod = ForgeEssentials.class, version=ForgeEssentials.CURRENT_MODULE_VERSION)
public class TeleportModule extends ConfigLoaderBase
{

    private static ForgeConfigSpec TELEPORT_CONFIG;
    private static final ConfigData data = new ConfigData("Teleport", TELEPORT_CONFIG, new ForgeConfigSpec.Builder());

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

    private PortalManager portalManager = new PortalManager();

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        FECommandManager.registerCommand(new CommandBack(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandBed(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandHome(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandSpawn(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandTp(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandTppos(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandWarp(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandTPA(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandPersonalWarp(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandTop(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandPortal(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandSetSpawn(true), event.getDispatcher());

        CommandJump jump = new CommandJump(true);
        FECommandManager.registerCommand(jump, event.getDispatcher());
        MinecraftForge.EVENT_BUS.register(jump);
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerStartingEvent event)
    {
        portalManager.load();

        APIRegistry.perms.registerPermissionProperty(PERM_TPA_TIMEOUT, "20",
                "Amount of sec a user has to accept a TPA request");

        APIRegistry.perms.registerPermission(PERM_BACK_ONDEATH, DefaultPermissionLevel.ALL,
                "Allow returning to the last death location with back-command");
        APIRegistry.perms.registerPermission(PERM_BACK_ONTP, DefaultPermissionLevel.ALL,
                "Allow returning to the last location before teleport with back-command");
        APIRegistry.perms.registerPermission(PERM_BED_OTHERS, DefaultPermissionLevel.OP,
                "Allow teleporting to other player's bed location");

        APIRegistry.perms.registerPermission(PERM_HOME, DefaultPermissionLevel.ALL, "Allow usage of /home");
        APIRegistry.perms.registerPermission(PERM_HOME_SET, DefaultPermissionLevel.ALL,
                "Allow setting of home location");
        APIRegistry.perms.registerPermission(PERM_HOME_OTHER, DefaultPermissionLevel.OP,
                "Allow setting other players home location");

        APIRegistry.perms.registerPermission(PERM_SPAWN_OTHERS, DefaultPermissionLevel.OP,
                "Allow setting other player's spawn");
        APIRegistry.perms.registerPermission(PERM_TOP_OTHERS, DefaultPermissionLevel.OP, "Use /top on others");
        APIRegistry.perms.registerPermission(PERM_TPA_SENDREQUEST, DefaultPermissionLevel.ALL,
                "Allow sending teleport-to requests");
        APIRegistry.perms.registerPermission(PERM_TPAHERE_SENDREQUEST, DefaultPermissionLevel.ALL,
                "Allow sending teleport-here requests");
        APIRegistry.perms.registerPermission(PERM_WARP_ADMIN, DefaultPermissionLevel.OP, "Administer warps");

        APIRegistry.perms.registerPermission(PERM_JUMP_TOOL, DefaultPermissionLevel.OP,
                "Allow jumping with a tool (default compass)");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerSleepInBed(PlayerSleepInBedEvent e)
    {
        if (e.getPlayer().level.isClientSide)
        {
            return;
        }

        if (!net.minecraftforge.event.ForgeEventFactory.fireSleepingLocationCheck(e.getPlayer(), e.getPos())
                || (e.getPlayer().isCrouching()))
        {
            ((ServerPlayer) e.getPlayer()).setRespawnPosition(e.getPlayer().level.dimension(), e.getPos(), 0,
                    false, false);
            ChatOutputHandler.chatConfirmation(e.getPlayer().createCommandSourceStack(), "Bed Position Set!");
        }
    }

    static ForgeConfigSpec.ConfigValue<String> FEportalBlock;

    @Override
    public void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.push("General");
        FEportalBlock = BUILDER.comment("Name of the block to use as material for new portals.\n"
                + "Does not override vanilla nether/end portals.\nSetting this to 'minecraft:portal' is currently not supported.")
                .define("portalBlock", "minecraft:glass_pane");
        BUILDER.pop();
    }

    @Override
    public void bakeConfig(boolean reload)
    {
        String portalBlockId = FEportalBlock.get();
        PortalManager.portalBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(portalBlockId));
    }

    @Override
    public ConfigData returnData()
    {
        return data;
    }
}
