package com.forgeessentials.teleport.commands;

import net.minecraft.block.material.Material;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.teleport.TeleportModule;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(CommandContext -> execute(CommandContext, "player")
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "me")
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("me"))
        {
            
            top(getServerPlayer(ctx.getSource()));
        }
        else if (params.equals("player") && hasPermission(ctx.getSource(), TeleportModule.PERM_TOP_OTHERS))
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            if (!player.hasDisconnected())
            {
                top(player);
            }
            else{
        		ChatOutputHandler.chatError(ctx.getSource(), Translator.format("Player %s does not exist, or is not online.", player.getDisplayName()));
        		return Command.SINGLE_SUCCESS;
        	}
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("me"))
        {
        	ChatOutputHandler.chatError(ctx.getSource(), "You are not a player.");
            return Command.SINGLE_SUCCESS;
        }
        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
        if (!player.hasDisconnected())
        {
            top(player);
        }
        else{
    		ChatOutputHandler.chatError(ctx.getSource(), Translator.format("Player %s does not exist, or is not online.", player.getDisplayName()));
    		return Command.SINGLE_SUCCESS;
    	}
        return Command.SINGLE_SUCCESS;
    }

    public void top(ServerPlayerEntity player) throws CommandException
    {
        WarpPoint point = new WarpPoint(player);
        int oldY = point.getBlockY();
        int precY = player.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING,player.blockPosition()).getY();

        if (oldY != precY)
        {
            if (!ForgeEssentials.isCubicChunksInstalled && precY == -1)
            {
                point.setY(0);
                while (player.level.getBlockState(point.getBlockPos()).getMaterial() != Material.AIR)
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
