package com.forgeessentials.teleport;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.RespawnHandler;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandSpawn extends ForgeEssentialsCommandBuilder
{

    @Override
    public String getPrimaryAlias()
    {
        return "spawn";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public String getPermissionNode()
    {
        return TeleportModule.PERM_SPAWN;
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, ServerPlayerEntity sender, String[] args) throws CommandException
    {
        if (args.length >= 1)
        {
            if (!PermissionAPI.hasPermission(sender, TeleportModule.PERM_SPAWN_OTHERS))
            {
                throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
            }
            ServerPlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player == null)
            {
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
            }

            WarpPoint point = RespawnHandler.getSpawn(player, null);
            if (point == null)
                throw new TranslatedCommandException("There is no spawnpoint set for that player.");
            TeleportHelper.teleport(player, point);
        }
        else if (args.length == 0)
        {
            ServerPlayerEntity player = sender;

            WarpPoint point = RespawnHandler.getSpawn(player, null);
            if (point == null)
            {
                throw new TranslatedCommandException("There is no spawnpoint set for that player.");
            }

            PlayerInfo.get(player.getPersistentID()).setLastTeleportOrigin(new WarpPoint(player));
            ChatOutputHandler.chatConfirmation(player, "Teleporting to spawn.");
            TeleportHelper.teleport(player, point);
        }
    }

    @Override
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
        }
        ServerPlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
        if (player == null)
        {
            throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }

        WarpPoint point = RespawnHandler.getSpawn(player, null);
        if (point == null)
        {
            throw new TranslatedCommandException("There is no spawnpoint set for that player.");
        }

        TeleportHelper.teleport(player, point);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return matchToPlayers(args);
        }
        else
        {
            return null;
        }
    }

}
