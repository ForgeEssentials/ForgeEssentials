package com.forgeessentials.teleport;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandTp extends ForgeEssentialsCommandBase
{

    /**
     * Spawn point for each dimension
     */
    public static HashMap<Integer, Point> spawnPoints = new HashMap<Integer, Point>();

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length == 1)
        {
            EntityPlayer target = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);

            if (target == null)
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
            TeleportHelper.teleport(sender, new WarpPoint(target));
        }
        else if (args.length == 2 && APIRegistry.perms.checkPermission(sender, TeleportModule.PERM_TP_OTHERS))
        {

            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                EntityPlayer target = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);

                if (target != null)
                {
                    PlayerInfo playerInfo = PlayerInfo.get(player.getPersistentID());
                    playerInfo.setLastTeleportOrigin(new WarpPoint(player));
                    WarpPoint point = new WarpPoint(target);
                    TeleportHelper.teleport(player, point);
                }
                else
                    throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[1]);
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
        else if (args.length >= 3)
        {
            if (args.length == 3)
            {
                EntityPlayerMP player = sender;
                double x = parseCoordinate(player.posX, args[0], true).getResult();
                double y = ServerUtil.parseYLocation(sender, player.posY, args[1]);
                double z = parseCoordinate(player.posZ, args[2], true).getResult();
                PlayerInfo playerInfo = PlayerInfo.get(player.getPersistentID());
                playerInfo.setLastTeleportOrigin(new WarpPoint(player));
                TeleportHelper.teleport(player, new WarpPoint(player.dimension, x, y, z, player.rotationPitch, player.rotationYaw));
            }
            else if (args.length == 4)
            {
                EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
                if (player != null)
                {
                    double x = parseCoordinate(player.posX, args[1], true).getResult();
                    double y = ServerUtil.parseYLocation(sender, player.posY, args[2]);
                    double z = parseCoordinate(player.posZ, args[3], true).getResult();
                    PlayerInfo playerInfo = PlayerInfo.get(player.getPersistentID());
                    playerInfo.setLastTeleportOrigin(new WarpPoint(player));
                    TeleportHelper.teleport(player, new WarpPoint(player.dimension, x, y, z, player.rotationPitch, player.rotationYaw));
                }
                else
                    throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
            }
            else
                throw new TranslatedCommandException("Improper syntax. Please try this instead: /tp [player] <player|<x> <y> <z>>");
        }
        else
            throw new TranslatedCommandException("Improper syntax. Please try this instead: /tp [player] <player|<x> <y> <z>>");
    }

    @Override
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 2)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                EntityPlayer target = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);

                if (target == null)
                    throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[1]);
                TeleportHelper.teleport(player, new WarpPoint(target));
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
        else if (args.length == 4)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                double x = parseCoordinate(player.posX, args[1], true).getResult();
                double y = ServerUtil.parseYLocation(sender, player.posY, args[2]);
                double z = parseCoordinate(player.posZ, args[3], true).getResult();
                TeleportHelper.teleport(player, new WarpPoint(player.dimension, x, y, z, player.rotationPitch, player.rotationYaw));
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
        else
        {
            ChatOutputHandler.chatError(sender, Translator.translate("Improper syntax. Please try this instead:"));
            ChatOutputHandler.chatNotification(sender, getUsage(sender));
        }
    }

    @Override public String getPrimaryAlias()
    {
        return "tp";
    }

    @Override public String getUsage(ICommandSender sender)
    {
        return "/tp [player] <player|<x> <y> <z>> Teleport to a location.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return TeleportModule.PERM_TP;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1 || args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        else
        {
            return Collections.emptyList();
        }
    }


    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }
}
