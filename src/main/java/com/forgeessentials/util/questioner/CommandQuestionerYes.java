package com.forgeessentials.util.questioner;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandQuestionerYes extends ForgeEssentialsCommandBuilder
{
    public CommandQuestionerYes(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "yes";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "accept" };
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.questioner";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .executes(CommandContext -> execute(CommandContext, "blank")
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        Questioner.answer(getServerPlayer(ctx.getSource()), true);
        return Command.SINGLE_SUCCESS;
    }
}
