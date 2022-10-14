package com.forgeessentials.core.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.CommandBlockLogic;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class ForgeEssentialsCommandBase extends CommandUtils
{
    // ------------------------------------------------------------
    // Command processing

	public LiteralArgumentBuilder<CommandSource> setExecution()
	{
    	return null;
	}

    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
    	ICommandSource source = CommandUtils.GetSource(ctx.getSource());
        if (source instanceof ServerPlayerEntity)
        {
            processCommandPlayer(ctx, params);
        }
        else if (source instanceof CommandBlockLogic)
        {
            processCommandBlock(ctx, params);
        }
        else
        {
            processCommandConsole(ctx, params);
        }
		return Command.SINGLE_SUCCESS;
    }

    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        throw new TranslatedCommandException("This command cannot be used as player");
    }

    public int processCommandConsole(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
    }

    public int processCommandBlock(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        return processCommandConsole(ctx, params);
    }
}
