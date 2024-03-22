package com.forgeessentials.chataddon.irc;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandIrcBot extends ForgeEssentialsCommandBuilder
{

    public CommandIrcBot(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "ircbot";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("info").executes(CommandContext -> execute(CommandContext, "info")))
                .then(Commands.literal("reconnect").executes(CommandContext -> execute(CommandContext, "reconnect")))
                .then(Commands.literal("disconnect").executes(CommandContext -> execute(CommandContext, "disconnect")));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("reconnect"))
        {
            ModuleIRCBridge.getInstance().connect();
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("disconnect"))
        {
            ModuleIRCBridge.getInstance().disconnect();
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("info"))
        {
            ChatOutputHandler.chatNotification(ctx.getSource(),
                    Translator.format("IRC bot is "+ (ModuleIRCBridge.getInstance().isConnected() ? "online, connected to "+ModuleIRCBridge.getInstance().getServer()+":"+ModuleIRCBridge.getInstance().getChannels() : "offline")));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }
}
