package com.forgeessentials.multiworld.v2.command;

import org.jetbrains.annotations.NotNull;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandMultiworld extends ForgeEssentialsCommandBuilder
{

    public CommandMultiworld(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "mw";
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "placeholder"));
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        return Command.SINGLE_SUCCESS;
    }
}
