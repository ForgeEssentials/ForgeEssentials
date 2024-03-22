package com.forgeessentials.core.commands;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.world.level.BaseCommandBlock;

public class CommandProcessor extends CommandUtils
{
    // ------------------------------------------------------------
    // Command processing

    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        try
        {
            if (params == null)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Command sent with null args Please report this!");
                LoggingHandler.felog
                        .error("Command Sent with null args by: " + ctx.getSource().getDisplayName().getString());
                LoggingHandler.felog.error("Please report this to the devs");
                return Command.SINGLE_SUCCESS;
            }
            CommandSource source = CommandUtils.GetSource(ctx.getSource());
            if (source instanceof ServerPlayer)
            {
                processCommandPlayer(ctx, params);
            }
            else if (source instanceof BaseCommandBlock)
            {
                processCommandBlock(ctx, params);
            }
            else if (source instanceof RconConsoleSource)
            {
                processCommandConsole(ctx, params);
            }
            else
            {
                processCommandConsole(ctx, params);
            }
        }
        catch (Exception e)
        {
            LoggingHandler.felog.error("Command Exception: " + e.getMessage());
            e.printStackTrace();
            if (e instanceof CommandSyntaxException)
            {
                throw new CommandSyntaxException(((CommandSyntaxException) e).getType(),
                        ((CommandSyntaxException) e).getRawMessage(), ((CommandSyntaxException) e).getInput(),
                        ((CommandSyntaxException) e).getCursor());
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ChatOutputHandler.chatError(ctx.getSource(), "This command cannot be used as player");
        return Command.SINGLE_SUCCESS;
    }

    public int processCommandConsole(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_CONSOLE_COMMAND);
        return Command.SINGLE_SUCCESS;
    }

    public int processCommandBlock(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        return processCommandConsole(ctx, params);
    }
}
