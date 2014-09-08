package com.forgeessentials.teleport;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.TeleportCenter;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;

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
        else if (args.length == 2 && PermissionsManager.checkPerm(sender, getPermissionNode() + ".others"))
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
                double x = parseDouble(sender, args[0]), y = parseDouble(sender, args[1]), z = parseDouble(sender, args[2]);
                PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.getPersistentID());
                playerInfo.back = new WarpPoint(player);
                TeleportCenter.addToTpQue(new WarpPoint(player.dimension, x, y, z, player.rotationPitch, player.rotationYaw), player);
            }
            else if (args.length == 4)
            {
                EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
                if (player != null)
                {
                    double x = parseDouble(sender, args[1]), y = parseDouble(sender, args[2]), z = parseDouble(sender, args[3]);
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
                OutputHandler.chatError(sender, "Improper syntax. Please try this instead: /tp [player] <player|<x> <y> <z>>");
            }
        }
        else
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: /tp [player] <player|<x> <y> <z>>");
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
                double x = parseDouble(sender, args[1]), y = parseDouble(sender, args[2]), z = parseDouble(sender, args[3]);
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
            ChatUtils.sendMessage(sender, getCommandUsage(sender));
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
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
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/tp [player] <player|<x> <y> <z>> Teleport to a location.";
    }
}
