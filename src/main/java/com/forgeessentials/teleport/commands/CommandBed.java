package com.forgeessentials.teleport.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.teleport.TeleportModule;
import com.forgeessentials.util.PlayerInfo;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandBed extends ForgeEssentialsCommandBuilder
{

    public CommandBed(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "bed";
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
        return TeleportModule.PERM_BED;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(CommandContext -> execute(CommandContext)
                                )
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (PermissionAPI.hasPermission(getServerPlayer(ctx.getSource()), TeleportModule.PERM_BED_OTHERS))
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            if (!player.hasDisconnected())
            {
                tp(player);
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", player.getDisplayName());
        }
        else
        {
            tp(getServerPlayer(ctx.getSource()));
        }
        return Command.SINGLE_SUCCESS;
    }

    private void tp(ServerPlayerEntity player) throws CommandException
    {
        World world = ServerLifecycleHooks.getCurrentServer().getLevel(player.getRespawnDimension());
        if (world == null)
            throw new TranslatedCommandException("No respawn dim found.");

        BlockPos spawn = player.getRespawnPosition();
        if (spawn == null)
            throw new TranslatedCommandException("No respawn position found.");

        PlayerInfo.get(player.getUUID()).setLastTeleportOrigin(new WarpPoint(player));
        WarpPoint spawnPoint = new WarpPoint(world.dimension(), spawn, player.xRot, player.yRot);
        TeleportHelper.teleport(player, spawnPoint);
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
        if (!player.hasDisconnected())
        {
            tp(player);
        }
        else
            throw new TranslatedCommandException("Player %s does not exist, or is not online.", player.getDisplayName());
        return Command.SINGLE_SUCCESS;
    }

}
