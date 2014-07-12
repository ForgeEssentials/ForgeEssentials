package com.forgeessentials.teleport;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.AreaSelector.Point;
import com.forgeessentials.util.AreaSelector.WarpPoint;
import com.forgeessentials.util.*;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.HashMap;
import java.util.List;

public class CommandTp extends ForgeEssentialsCommandBase {

    /**
     * Spawn point for each dimension
     */
    public static HashMap<Integer, Point> spawnPoints = new HashMap<Integer, Point>();

    @Override
    public String getCommandName()
    {
        return "tp";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 1)
        {
            EntityPlayer target = FunctionHelper.getPlayerForName(sender, args[0]);
            if (PlayerSelector.hasArguments(args[0]))
            {
                target = FunctionHelper.getPlayerForName(sender, args[0]);
            }
            if (target != null)
            {
                EntityPlayerMP player = (EntityPlayerMP) sender;
                PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.getPersistentID());
                playerInfo.back = new WarpPoint(player);
                CommandBack.justDied.remove(player.getPersistentID());
                TeleportCenter.addToTpQue(new WarpPoint(target), player);
            }
            else
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
        else if (args.length == 2 && APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
        {

            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
            if (player != null)
            {
                EntityPlayer target = FunctionHelper.getPlayerForName(sender, args[1]);

                if (target != null)
                {
                    PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.getPersistentID());
                    playerInfo.back = new WarpPoint(player);
                    WarpPoint point = new WarpPoint(target);
                    FunctionHelper.setPlayer(player, point);
                }
                else
                {
                    OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[1]));
                    return;
                }
            }
            else
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
                return;
            }
        }
        else if (args.length >= 3)
        {
            if (args.length == 3)
            {
                EntityPlayerMP player = (EntityPlayerMP) sender;
                int x = parseInt(sender, args[0], player.posX), y = parseInt(sender, args[1], player.posY), z = parseInt(sender, args[2], player.posZ);
                PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.getPersistentID());
                playerInfo.back = new WarpPoint(player);
                TeleportCenter.addToTpQue(new WarpPoint(player.dimension, x, y, z, player.rotationPitch, player.rotationYaw), player);
            }
            else if (args.length == 4)
            {
                EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
                if (player != null)
                {
                    int x = parseInt(sender, args[1], player.posX), y = parseInt(sender, args[2], player.posY), z = parseInt(sender, args[3], player.posZ);
                    PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.getPersistentID());
                    playerInfo.back = new WarpPoint(player);
                    TeleportCenter.addToTpQue(new WarpPoint(player.dimension, x, y, z, player.rotationPitch, player.rotationYaw), player);
                }
                else
                {
                    OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
                }
            }
            else
            {
                OutputHandler.chatError(sender, "Improper syntax. Please try this instead: ");
            }
        }
        else
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: ");
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length == 2)
        {
            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
            if (player != null)
            {
                EntityPlayer target = FunctionHelper.getPlayerForName(sender, args[0]);
                if (PlayerSelector.hasArguments(args[1]))
                {
                    target = FunctionHelper.getPlayerForName(sender, args[1]);
                }
                if (target != null)
                {
                    PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.getPersistentID());
                    playerInfo.back = new WarpPoint(player);
                    TeleportCenter.addToTpQue(new WarpPoint(target), player);
                }
                else
                {
                    OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[1]));
                    return;
                }
            }
            else
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
                return;
            }
        }
        else if (args.length == 4)
        {
            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
            if (player != null)
            {
                int x = parseInt(sender, args[1], player.posX), y = parseInt(sender, args[2], player.posY), z = parseInt(sender, args[3], player.posZ);
                PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.getPersistentID());
                playerInfo.back = new WarpPoint(player);
                TeleportCenter.addToTpQue(new WarpPoint(player.dimension, x, y, z, player.rotationPitch, player.rotationYaw), player);
            }
            else
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
        else
        {
            ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: ");
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getCommandPerm()
    {
        return "fe.teleport.tp";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1 || args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.OWNERS;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/tp [player] <player|<x> <y> <z>> Teleport to a location.";
    }
}
