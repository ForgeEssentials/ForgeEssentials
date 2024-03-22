package com.forgeessentials.economy.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandRequestPayment extends ForgeEssentialsCommandBuilder
{

    public CommandRequestPayment(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "requestpayment";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("amount", IntegerArgumentType.integer())
                        .executes(CommandContext -> execute(CommandContext, "blank"))));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("You requested %s to pay %s",
                player.getDisplayName().getString(), APIRegistry.economy.toString(amount)));
        ChatOutputHandler.chatNotification(player, "You have been requested to pay %s by %s",
                APIRegistry.economy.toString(amount), getServerPlayer(ctx.getSource()).getDisplayName().getString());
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("You requested %s to pay %s",
                player.getDisplayName().getString(), APIRegistry.economy.toString(amount)));
        ChatOutputHandler.chatNotification(player, "You have been requested to pay %s by the server",
                APIRegistry.economy.toString(amount), getServerPlayer(ctx.getSource()).getDisplayName().getString());
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
