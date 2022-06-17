package com.forgeessentials.teleport;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

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
    public void processCommandPlayer(MinecraftServer server, ServerPlayerEntity sender, String[] args) throws CommandException
    {
        if (args.length == 1)
        {
            PlayerEntity target = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);

            if (target == null)
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
            TeleportHelper.teleport(sender, new WarpPoint(target));
        }
        else if (args.length == 2 && APIRegistry.perms.checkPermission(sender, TeleportModule.PERM_TP_OTHERS))
        {

            ServerPlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                PlayerEntity target = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);

                if (target != null)
                {
                    PlayerInfo playerInfo = PlayerInfo.get(player.getUUID());
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
                ServerPlayerEntity player = sender;
                double x = parseCoordinate(player.position().x, args[0], true).getResult();
                double y = ServerUtil.parseYLocation(sender.createCommandSourceStack(), player.position().y, args[1]);
                double z = parseCoordinate(player.position().z, args[2], true).getResult();
                PlayerInfo playerInfo = PlayerInfo.get(player.getUUID());
                playerInfo.setLastTeleportOrigin(new WarpPoint(player));
                TeleportHelper.teleport(player, new WarpPoint(player.level.dimension(), x, y, z, player.xRot, player.yRot));
            }
            else if (args.length == 4)
            {
                ServerPlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
                if (player != null)
                {
                    double x = parseCoordinate(player.position().x, args[1], true).getResult();
                    double y = ServerUtil.parseYLocation(sender.createCommandSourceStack(), player.position().y, args[2]);
                    double z = parseCoordinate(player.position().z, args[3], true).getResult();
                    PlayerInfo playerInfo = PlayerInfo.get(player.getUUID());
                    playerInfo.setLastTeleportOrigin(new WarpPoint(player));
                    TeleportHelper.teleport(player, new WarpPoint(player.level.dimension(), x, y, z, player.xRot, player.yRot));
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
            ServerPlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                PlayerEntity target = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);

                if (target == null)
                    throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[1]);
                TeleportHelper.teleport(player, new WarpPoint(target));
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
        else if (args.length == 4)
        {
            ServerPlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                double x = parseCoordinate(player.position().x, args[1], true).getResult();
                double y = ServerUtil.parseYLocation(sender, player.position().y, args[2]);
                double z = parseCoordinate(player.position().z, args[3], true).getResult();
                TeleportHelper.teleport(player, new WarpPoint(player.level.dimension(), x, y, z, player.xRot, player.yRot));
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

    @Override
    public String getPrimaryAlias()
    {
        return "tp";
    }

    @Override
    public String getUsage(ICommandSender sender)
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
