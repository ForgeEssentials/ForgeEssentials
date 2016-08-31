package com.forgeessentials.playerlogger;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;

public class PlayerLoggerEventHandler extends ServerEventHandler
{

    public static class LoggerCheckInfo
    {

        public WorldPoint checkPoint;

        public long checkStartId;

    }

    public Map<EntityPlayer, LoggerCheckInfo> playerInfo = new WeakHashMap<>();

    public static int pickerRange = 0;

    public static int eventType = 0b1111;

    public static String searchCriteria = "";

    private WorldArea getAreaAround(WorldPoint wp)
    {
        return getAreaAround(wp, pickerRange);
    }

    private WorldArea getAreaAround(WorldPoint wp, int radius)
    {
        return new WorldArea(wp.getDimension(),
                new Point(wp.getX() - radius, wp.getY() - radius, wp.getZ() - radius),
                new Point(wp.getX() + radius, wp.getY() + radius, wp.getZ() + radius));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
        if (stack == null || stack.getItem() != Items.CLOCK)
            return;
        if (!(event instanceof LeftClickBlock || event instanceof RightClickBlock))
            return;
        if (!APIRegistry.perms.checkPermission(event.getEntityPlayer(), ModulePlayerLogger.PERM_WAND))
            return;
        event.setCanceled(true);

        LoggerCheckInfo info = playerInfo.get(event.getEntityPlayer());
        if (info == null)
        {
            info = new LoggerCheckInfo();
            playerInfo.put(event.getEntityPlayer(), info);
        }

        WorldPoint point;
        if (event instanceof RightClickBlock)
            point = new WorldPoint(event.getEntityPlayer().dimension, //
                    event.getPos().getX() + event.getFace().getFrontOffsetX(), //
                    event.getPos().getY() + event.getFace().getFrontOffsetY(), //
                    event.getPos().getZ() + event.getFace().getFrontOffsetZ());
        else
            point = new WorldPoint(event.getEntityPlayer().dimension, event.getPos());

        boolean newCheck = !point.equals(info.checkPoint);
        if (newCheck)
        {
            info.checkPoint = point;
            info.checkStartId = 0;
            if (event instanceof RightClickBlock)
                ChatOutputHandler.chatNotification(event.getEntityPlayer(), "Showing recent block changes (clicked side):");
            else
                ChatOutputHandler.chatNotification(event.getEntityPlayer(), "Showing recent block changes (clicked block):");
        }

        if ((0b00100 & eventType) != 0)
        {
            List<Action01Block> changes = ModulePlayerLogger.getLogger().getLoggedBlockChanges(getAreaAround(point), null, null, info.checkStartTime, 4);

            if (changes.size() == 0 && !newCheck)
            {
                ChatOutputHandler.chatError(event.getEntityPlayer(), "No more changes");
                return;
            }

            for (Action01Block change : changes)
            {
                info.checkStartId = change.id;

                String msg = String.format("%1$tm/%1$te %1$tH:%1$tM:%1$tS", change.time);
                if (change.player != null)
                {
                    UserIdent player = UserIdent.get(change.player.uuid);
                    msg += " " + player.getUsernameOrUuid();
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
                case BURN:
                    msg += String.format("BURN %s", blockName);
                    break;
                default:
                    continue;
                }
                ChatOutputHandler.chatConfirmation(event.getEntityPlayer(), msg);
            }
        }
        // Add other Action events (Command, Player, Explosion, etc)
    }

}
