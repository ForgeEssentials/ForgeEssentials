package com.forgeessentials.economy.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandRequestPayment extends ForgeEssentialsCommandBuilder
{

    public CommandRequestPayment(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "requestpayment";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(CommandContext -> execute(CommandContext, "blank")
                                        )
                                )
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        ChatOutputHandler.chatConfirmation(ctx.getSource(),
                Translator.format("You requested %s to pay %s", player.getName(), APIRegistry.economy.toString(amount)));
        ChatOutputHandler.chatConfirmation(player,
                Translator.format("You have been requested to pay %s by %s", APIRegistry.economy.toString(amount), getServerPlayer(ctx.getSource()).getDisplayName().getString()));
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        ChatOutputHandler.chatConfirmation(ctx.getSource(),
                Translator.format("You requested %s to pay %s", player.getName(), APIRegistry.economy.toString(amount)));
        ChatOutputHandler.chatConfirmation(player,
                Translator.format("You have been requested to pay %s by the server", APIRegistry.economy.toString(amount), getServerPlayer(ctx.getSource()).getDisplayName().getString()));
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.economy." + getName();
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {

        return DefaultPermissionLevel.ALL;
    }
}
