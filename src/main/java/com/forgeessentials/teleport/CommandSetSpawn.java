package com.forgeessentials.teleport;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.permissions.commands.PermissionCommandParser;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandSetSpawn extends ForgeEssentialsCommandBuilder
{

    public CommandSetSpawn(boolean enabled)
    {
        super(enabled);
    }

    public static final String PERM_SETSPAWN = "fe.perm.setspawn";

    @Override
    public String getPrimaryAlias()
    {
        return "setspawn";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return PERM_SETSPAWN;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("bed")
                        .then(Commands.literal("enable")
                                .executes(CommandContext -> execute(CommandContext, "here-enable")
                                        )
                                )
                        .then(Commands.literal("true")
                                .executes(CommandContext -> execute(CommandContext, "here-enable")
                                        )
                                )
                        .then(Commands.literal("disable")
                                .executes(CommandContext -> execute(CommandContext, "here-disable")
                                        )
                                )
                        .then(Commands.literal("false")
                                .executes(CommandContext -> execute(CommandContext, "here-disable")
                                        )
                                )
                        )
                .then(Commands.literal("here")
                        .executes(CommandContext -> execute(CommandContext, "here")
                                )
                        )
                .then(Commands.literal("clear")
                        .then(Commands.argument("position", BlockPosArgument.blockPos())
                                .then(Commands.argument("dim", DimensionArgument.dimension())
                                        .executes(CommandContext -> execute(CommandContext, "clear")
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("help")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        String group = Zone.GROUP_DEFAULT;
        Zone zone = APIRegistry.perms.getServerZone();
        checkPermission(ctx.getSource(), PermissionCommandParser.PERM_GROUP_SPAWN);
        if (params.toString().equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/setspawn here|clear|<x> <y> <z> <dim>: Set spawn location");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/setspawn bed (enable|disable): Enable/disable spawning at bed");
            return Command.SINGLE_SUCCESS;
        }

        String[] args = params.toString().split("-");
        WarpPoint point = null;
        switch (args[0])
        {
        case "here":
            if (getServerPlayer(ctx.getSource()) == null)
                throw new TranslatedCommandException("[here] cannot be used from console.");
            point = new WarpPoint(getServerPlayer(ctx.getSource()));
            break;
        case "bed":
        {
            String val = args[1];
            if (val.equals("enable"))
            {
                zone.setGroupPermission(group, FEPermissions.SPAWN_BED, true);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Enabled bed-spawning for group %s in zone %s", group, zone.getName());
            }
            else if (val.equals("disable"))
            {
                zone.setGroupPermission(group, FEPermissions.SPAWN_BED, false);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Disabled bed-spawning for group %s in zone %s", group, zone.getName());
            }
            else
                ChatOutputHandler.chatError(ctx.getSource(), "Invalid argument. Use enable or disable.");
            return Command.SINGLE_SUCCESS;
        }
        case "clear":
            point = null;
            break;
        default:
            try
            {
                BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "position");
                ServerWorld dimension = DimensionArgument.getDimension(ctx, "dim");
                point = new WarpPoint(dimension, pos, 0, 0);
            }
            catch (NumberFormatException e)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Invalid location argument");
                return Command.SINGLE_SUCCESS;
            }
            break;
        }

        if (point == null)
        {
            zone.clearGroupPermission(group, FEPermissions.SPAWN_LOC);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Cleared spawn-rule for group %s in zone %s", group, zone.getName());
        }
        else
        {
            zone.setGroupPermissionProperty(group, FEPermissions.SPAWN_LOC, point.toString());
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set spawn for group %s to %s in zone %s", group, point.toString(), zone.getName());
        }
        return Command.SINGLE_SUCCESS;
    }

}
