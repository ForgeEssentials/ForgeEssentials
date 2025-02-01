package com.forgeessentials.teleport;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.RespawnHandler;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;



public class CommandSpawn extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "fespawn";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "spawn" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
        {
            return "/spawn [player] Teleport you or another player to their spawn point.";
        }
        else
        {
            return "/spawn <player> Teleport a player to their spawn point.";
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public String getPermissionNode()
    {
        return TeleportModule.PERM_SPAWN;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length >= 1)
        {
            if (!PermissionManager.checkPermission(sender, TeleportModule.PERM_SPAWN_OTHERS))
            {
                throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
            }
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
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
            EntityPlayerMP player = sender;

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
    public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
        }

        if (!PermissionManager.checkPermission(sender, this, TeleportModule.PERM_SPAWN_OTHERS))
        {
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
        }
        EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
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
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

}
