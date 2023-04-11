package com.forgeessentials.core.commands;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.tileentity.CommandBlockLogic;

public class CommandProcessor extends CommandUtils
{
    // ------------------------------------------------------------
    // Command processing

    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        try {
            if(params==null) {
                ChatOutputHandler.chatError(ctx.getSource(), "Command sent with null args Please report this!");
                LoggingHandler.felog.error("Command Sent with null args by: " + ctx.getSource().getDisplayName().getString());
                LoggingHandler.felog.error("Please report this to the devs");
                return Command.SINGLE_SUCCESS;
            }
            ICommandSource source = CommandUtils.GetSource(ctx.getSource());
            if (source instanceof ServerPlayerEntity)
            {
                processCommandPlayer(ctx, params);
            }
            else if (source instanceof CommandBlockLogic)
            {
                processCommandBlock(ctx, params);
            }
            else if (source instanceof RConConsoleSource)
            {
                processCommandConsole(ctx, params);
            }
            else
            {
                processCommandConsole(ctx, params);
            }
        }
        catch(Exception e) {
            LoggingHandler.felog.error("Command Exception: "+e.getMessage());
            e.printStackTrace();
            if(e instanceof CommandSyntaxException) {
                throw new CommandSyntaxException(((CommandSyntaxException)e).getType(), ((CommandSyntaxException)e).getRawMessage(), ((CommandSyntaxException)e).getInput(), ((CommandSyntaxException)e).getCursor());
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        throw new TranslatedCommandException("This command cannot be used as player");
    }

    public int processCommandConsole(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
    }

    public int processCommandBlock(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        return processCommandConsole(ctx, params);
    }
}
