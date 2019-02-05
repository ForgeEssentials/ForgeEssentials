package com.forgeessentials.playerlogger;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.playerlogger.FilterConfig.ActionEnum;
import com.forgeessentials.playerlogger.entity.Action;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.Action02Command;
import com.forgeessentials.playerlogger.entity.Action03PlayerEvent;
import com.forgeessentials.playerlogger.entity.Action04PlayerPosition;
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
        CheckBlock(point, fc, FMLCommonHandler.instance().getMinecraftServerInstance());
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
    public void CheckBlock(WorldPoint point, FilterConfig fc, ICommandSender sender, int pageSize, boolean newCheck, net.minecraftforge.event.entity.player.PlayerInteractEvent action)
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
            if (action instanceof RightClickBlock)
                ChatOutputHandler.chatNotification(sender, "Showing recent block changes (clicked side):");
            else
                ChatOutputHandler.chatNotification(sender, "Showing recent block changes (clicked block):");
        }

        ChatOutputHandler.chatNotification(sender, "Loading logs from database!  This may take a while.");
        List<Action> changes = ModulePlayerLogger.getLogger().getLoggedActions(getAreaAround(point,fc.pickerRange),fc.After(),fc.Before(),info.checkStartId,pageSize);

        //List<Action01Block> changes = ModulePlayerLogger.getLogger().getLoggedBlockChanges(getAreaAround(point, fc.pickerRange),fc.After(), fc.Before(), info.checkStartId, pageSize);

        if (changes.size() == 0 && !newCheck)
        {
            ChatOutputHandler.chatError(sender, "No more changes");
            return;
        }

        for (Action change : changes)
        {
            info.checkStartId = change.id;

            String msg = String.format("%1$tm/%1$te %1$tH:%1$tM:%1$tS", change.time);
            if (change.player != null)
            {
                UserIdent player = UserIdent.get(change.player.uuid);
                msg += " " + player.getUsernameOrUuid();
                if (fc.player != null && fc.player != player) {
                    continue;
                }
            } else if (fc.player != null) {
                continue;
            }
            msg += ": ";
            if (change instanceof Action01Block)
            {
                Action01Block change2 = (Action01Block) change;
                String blockName = change2.block != null ? change2.block.name : "";
                if (!fc.hasBlock((Block) Block.getBlockFromName(blockName)))
                    continue;

                if (blockName.contains(":"))
                    blockName = blockName.split(":", 2)[1];



                switch (change2.type)
                {
                case PLACE:
                    if (!fc.hasAction(ActionEnum.blockPlace))
                        continue;
                    msg += String.format("PLACED %s", blockName);
                    break;
                case BREAK:
                    if (!fc.hasAction(ActionEnum.blockBreak))
                        continue;
                    msg += String.format("BROKE %s", blockName);
                    break;
                case DETONATE:
                    if (!fc.hasAction(ActionEnum.blockDetonate))
                        continue;
                    msg += String.format("EXPLODED %s", blockName);
                    break;
                case USE_LEFT:
                    if (!fc.hasAction(ActionEnum.blockUse_Left))
                        continue;
                    msg += String.format("LEFT CLICK %s", blockName);
                    break;
                case USE_RIGHT:
                    if (!fc.hasAction(ActionEnum.blockUse_Right))
                        continue;
                    msg += String.format("RIGHT CLICK %s", blockName);
                    break;
                case BURN:
                    if (!fc.hasAction(ActionEnum.blockBurn))
                        continue;
                    msg += String.format("BURN %s", blockName);
                    break;
                default:
                    continue;
                }
            }
            else if (change instanceof Action02Command)
            {
                if (!fc.hasAction(ActionEnum.command))
                    continue;
                Action02Command change2 = (Action02Command) change;
                String command = change2.command;
                String args = change2.arguments;
                msg += String.format("Ran Command: %s with args: %s",command,args);
            }
            else if (change instanceof Action03PlayerEvent)
            {
                Action03PlayerEvent change2 = (Action03PlayerEvent) change;
                switch (change2.type)
                {
                case LOGIN:
                    if (!fc.hasAction(ActionEnum.playerLogin))
                        continue;
                    msg += String.format("Logged In at %d %d %d", change2.x, change2.y, change2.z);
                    break;
                case LOGOUT:
                    if (!fc.hasAction(ActionEnum.playerLogout))
                        continue;
                    msg += String.format("Logged Out at %d %d %d", change2.x, change2.y, change2.z);
                    break;
                case RESPAWN:
                    if (!fc.hasAction(ActionEnum.playerRespawn))
                        continue;
                    msg += String.format("Respawned at %d %d %d", change2.x, change2.y, change2.z);
                    break;
                case CHANGEDIM:
                    if (!fc.hasAction(ActionEnum.playerChangeDim))
                        continue;
                    msg += String.format("Changed Dim at %d %d %d", change2.x, change2.y, change2.z);
                    break;
                case MOVE:
                    if (!fc.hasAction(ActionEnum.playerPosition))
                        continue;
                    msg += String.format("Position at %d %d %d", change2.x, change2.y, change2.z);
                    break;
                default:
                    continue;
                }
            }
            else if (change instanceof Action04PlayerPosition)
            {
                if (!fc.hasAction(ActionEnum.playerPosition))
                    continue;
                Action04PlayerPosition change2 = (Action04PlayerPosition) change;
                msg += String.format("Position is %d %d %d", change2.x, change2.y, change2.z);
            }

            ChatOutputHandler.chatConfirmation(sender, msg);
        }

        if (pageSize == 0)
            playerInfo.remove(sender);

        // Add other Action events (Command, Player, Explosion, etc)

    }
}
