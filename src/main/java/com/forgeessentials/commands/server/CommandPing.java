package com.forgeessentials.commands.server;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandPing extends ForgeEssentialsCommandBuilder
{
    public CommandPing(boolean enabled)
    {
        super(enabled);
    }

    public static String response = "Pong! %time";

    @Override
    public String getPrimaryAlias()
    {
        return "ping";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".ping";
    }

    public LiteralArgumentBuilder<CommandSource> setExecution()
	{
        return baseBuilder
                .executes(CommandContext -> execute(CommandContext, response)
                        );
	}

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ChatOutputHandler.chatNotification(ctx.getSource(), response.replaceAll("%time", ((ServerPlayerEntity) ctx.getSource().getEntity()).latency + "ms."));
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ChatOutputHandler.chatNotification(ctx.getSource(), response.replaceAll("%time", "Server has blazing fast speeds!"));
        return Command.SINGLE_SUCCESS;
    }
}
