package com.forgeessentials.util.selections;

import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandExpandY extends ForgeEssentialsCommandBuilder
{

    public CommandExpandY(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "SELexpandY";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        Selection sel = SelectionHandler.getSelection(getServerPlayer(ctx.getSource()));
        if (sel == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Invalid selection.");
            return Command.SINGLE_SUCCESS;
        }
        SelectionHandler.setStart(getServerPlayer(ctx.getSource()), sel.getStart().setY(0));
        SelectionHandler.setEnd(getServerPlayer(ctx.getSource()),
                sel.getEnd().setY(ctx.getSource().getLevel().getMaxBuildHeight()));
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Selection expanded from bottom to top.");
        SelectionHandler.sendUpdate(getServerPlayer(ctx.getSource()));
        return Command.SINGLE_SUCCESS;
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

}
