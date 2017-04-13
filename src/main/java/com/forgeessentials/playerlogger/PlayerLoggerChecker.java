package com.forgeessentials.playerlogger;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.util.output.ChatOutputHandler;

public class PlayerLoggerChecker
{
    public static PlayerLoggerChecker instance = new PlayerLoggerChecker();

    private PlayerLoggerChecker()
    {

    }


    private WorldArea getAreaAround(WorldPoint wp)
    {
        return getAreaAround(wp, FilterConfig.globalConfig.pickerRange);
    }

    private WorldArea getAreaAround(WorldPoint wp, int radius)
    {
        return new WorldArea(wp.getDimension(),
                new Point(wp.getX() - radius, wp.getY() - radius, wp.getZ() - radius),
                new Point(wp.getX() + radius, wp.getY() + radius, wp.getZ() + radius));
    }

    public static class LoggerCheckInfo
    {

        public WorldPoint checkPoint;

        public long checkStartId;

    }

    public Map<ICommandSender, LoggerCheckInfo> playerInfo = new WeakHashMap<>();

    public void CheckBlock(WorldPoint point, FilterConfig fc)
    {
        CheckBlock(point, fc, MinecraftServer.getServer());
    }
    public void CheckBlock(WorldPoint point, FilterConfig fc, ICommandSender sender)
    {
        CheckBlock(point, fc, sender,4);
    }

    public void CheckBlock(WorldPoint point, FilterConfig fc, ICommandSender sender, int pageSize)
    {
        CheckBlock(point, fc, sender, pageSize,false);
    }

    public void CheckBlock(WorldPoint point, FilterConfig fc, ICommandSender sender, int pageSize, boolean newCheck)
    {
        CheckBlock(point, fc, sender, pageSize,newCheck, null);
    }
    public void CheckBlock(WorldPoint point, FilterConfig fc, ICommandSender sender, int pageSize, boolean newCheck, Action action)
    {
        LoggerCheckInfo info = playerInfo.get(sender);
        if (info == null)
        {
            info = new LoggerCheckInfo();
            playerInfo.put(sender, info);
        }

        newCheck |=  !point.equals(info.checkPoint);
        if (newCheck)
        {
            info.checkPoint = point;
            info.checkStartId = 0;
            if (action == Action.RIGHT_CLICK_BLOCK)
                ChatOutputHandler.chatNotification(sender, "Showing recent block changes (clicked side):");
            else
                ChatOutputHandler.chatNotification(sender, "Showing recent block changes (clicked block):");
        }

        List<Action01Block> changes = ModulePlayerLogger.getLogger().getLoggedBlockChanges(getAreaAround(point, fc.pickerRange),fc.After(), fc.Before(), info.checkStartId, pageSize);

        if (changes.size() == 0 && !newCheck)
        {
            ChatOutputHandler.chatError(sender, "No more changes");
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
            ChatOutputHandler.chatConfirmation(sender, msg);
        }

        if (pageSize == 0)
            playerInfo.remove(sender);

        // Add other Action events (Command, Player, Explosion, etc)

    }
}
