package com.forgeessentials.chat.command;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandUnmute extends ForgeEssentialsCommandBuilder
{

    public CommandUnmute(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "unmute";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat.mute";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.then(Commands.argument("player", EntityArgument.player())
                .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayerEntity receiver = EntityArgument.getPlayer(ctx, "player");
        if (receiver.hasDisconnected())
        {
            ChatOutputHandler.chatError(ctx.getSource(), Translator
                    .format("Player %s does not exist, or is not online.", receiver.getDisplayName().getString()));
            return Command.SINGLE_SUCCESS;
        }

        PlayerUtil.getPersistedTag(receiver, false).remove("mute");
        ChatOutputHandler.chatError(ctx.getSource(),
                Translator.format("You unmuted %s.", receiver.getDisplayName().getString()));
        ChatOutputHandler.chatError(receiver,
                Translator.format("You were unmuted by %s.", ctx.getSource().getDisplayName().getString()));
        return Command.SINGLE_SUCCESS;
    }
}
