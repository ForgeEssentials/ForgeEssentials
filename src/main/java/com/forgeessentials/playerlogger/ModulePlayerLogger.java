package com.forgeessentials.playerlogger;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.playerlogger.command.CommandRollback;
import com.forgeessentials.playerlogger.entity.ActionBlock;
import com.forgeessentials.playerlogger.network.S2PacketPlayerLogger;
import com.forgeessentials.playerlogger.network.S3PacketRollback;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@FEModule(name = "PlayerLogger", parentMod = ForgeEssentials.class)
public class ModulePlayerLogger
{

    public static final String PERM = "fe.pl";
    public static final String PERM_WAND = PERM + ".wand";

    private static PlayerLogger logger;

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        logger = new PlayerLogger();
        ForgeEssentials.getConfigManager().registerLoader("PlayerLogger", new PlayerLoggerConfig());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent e)
    {
        FunctionHelper.netHandler.registerMessage(S2PacketPlayerLogger.class, S2PacketPlayerLogger.class, 2, Side.CLIENT);
        FunctionHelper.netHandler.registerMessage(S3PacketRollback.class, S3PacketRollback.class, 3, Side.CLIENT);
    }

    @SubscribeEvent
    public void serverPreInit(FEModuleServerPreInitEvent e)
    {
        registerPermissions(APIRegistry.perms);
        logger.loadDatabase();
        new CommandRollback().register();
        // new CommandTestPlayerlogger().register();
    }

    private void registerPermissions(IPermissionsHelper p)
    {
        p.registerPermission(PERM, RegisteredPermValue.OP, "Player logger permisssions");
        p.registerPermission(PERM_WAND, RegisteredPermValue.OP, "Allow usage of player loggger wand (clock)");
    }

    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent e)
    {
        if (logger != null)
            logger.close();
    }

    public static PlayerLogger getLogger()
    {
        return logger;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (!APIRegistry.perms.checkPermission(event.entityPlayer, ModulePlayerLogger.PERM_WAND))
            return;
        ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
        if (stack == null || stack.getItem() != Items.clock)
            return;
        if (event.action == Action.RIGHT_CLICK_AIR)
            return;
        event.setCanceled(true);

        WorldPoint point = new WorldPoint(event.entityPlayer.dimension, event.x, event.y, event.z);
        if (event.action == Action.RIGHT_CLICK_BLOCK)
        {
            point = new WorldPoint(event.entityPlayer.dimension, //
                    event.x + Facing.offsetsXForSide[event.face], //
                    event.y + Facing.offsetsYForSide[event.face], //
                    event.z + Facing.offsetsZForSide[event.face]);
            OutputHandler.chatNotification(event.entityPlayer, "Showing recent block changes (clicked side):");
        }
        else
        {
            OutputHandler.chatNotification(event.entityPlayer, "Showing recent block changes (clicked block):");
        }

        List<ActionBlock> changes = ModulePlayerLogger.getLogger().getBlockChanges(point, 4);
        for (ActionBlock change : changes)
        {
            String msg = String.format("%1$tm/%1$te %1$tH:%1$tM:%1$tS", change.time);
            if (change.player != null)
            {
                UserIdent player = UserIdent.get(change.player.uuid);
                msg += " " + player.getUsernameOrUUID();
            }
            msg += ": ";

            String blockName = change.block != null ? change.block.name : "";
            if (blockName.contains(":"))
                blockName = blockName.split(":", 2)[1];

            switch (change.type)
            {
            case PLACE:
                msg += String.format("PLACED %s", blockName);
                break;
            case BREAK:
                msg += String.format("BROKE %s", blockName);
                break;
            case DETONATE:
                msg += String.format("EXPLODED %s", blockName);
                break;
            case USE_LEFT:
                msg += String.format("LEFT CLICK %s", blockName);
                break;
            case USE_RIGHT:
                msg += String.format("RIGHT CLICK %s", blockName);
                break;
            default:
                continue;
            }
            OutputHandler.chatConfirmation(event.entityPlayer, msg);
        }
    }
}
