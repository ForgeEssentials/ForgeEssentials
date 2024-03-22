package com.forgeessentials.chat.command;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandUnmute extends ForgeEssentialsCommandBuilder
{

    public CommandUnmute(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "unmute";
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("player", EntityArgument.player())
                .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer receiver = EntityArgument.getPlayer(ctx, "player");
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
