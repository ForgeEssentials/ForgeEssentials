package com.forgeessentials.chataddon.irc;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import org.jetbrains.annotations.NotNull;

import com.forgeessentials.api.permissions.DefaultPermissionLevel;
import com.forgeessentials.commons.chat.TextComponent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandIrc extends ForgeEssentialsCommandBuilder
{

    public CommandIrc(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "irc";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("message", StringArgumentType.greedyString())
                .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (!ModuleIRCBridge.getInstance().isConnected())
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Not connected to IRC!");
            return Command.SINGLE_SUCCESS;
        }
        ModuleIRCBridge.getInstance().sendPlayerMessage(ctx.getSource(),
                new TextComponent(StringArgumentType.getString(ctx, "message")));
        return Command.SINGLE_SUCCESS;
    }
}
