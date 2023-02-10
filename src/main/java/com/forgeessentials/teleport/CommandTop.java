package com.forgeessentials.teleport;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;

public class CommandTop extends ForgeEssentialsCommandBuilder
{

    public CommandTop(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "top";
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
        return TeleportModule.PERM_TOP;
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, ServerPlayerEntity sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            top(sender);
        }
        else if (args.length == 1 && PermissionAPI.hasPermission(sender, TeleportModule.PERM_TOP_OTHERS))
        {
            ServerPlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                top(player);
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
        else
            throw new TranslatedCommandException("Improper syntax. Please try this instead: <player>");
    }

    @Override
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 1)
        {
            ServerPlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                top(player);
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
        else
            throw new TranslatedCommandException("Improper syntax. Please try this instead: <player>");
    }

    public void top(ServerPlayerEntity player) throws CommandException
    {
        WarpPoint point = new WarpPoint(player);
        int oldY = point.getBlockY();
        int precY = player.world.getPrecipitationHeight(player.getPosition()).getY();

        if (oldY != precY)
        {
            if (!ForgeEssentials.isCubicChunksInstalled && precY == -1)
            {
                point.setY(0);
                while (player.world.getBlockState(point.getBlockPos()).getMaterial() != Material.AIR)
                {
                    point.setY(point.getY() + 1);
                }
                if (oldY == point.getBlockY())
                {
                    return;
                }
            }
            else
            {
                point.setY(precY);
            }
            TeleportHelper.teleport(player, point);
        }
    }

}
