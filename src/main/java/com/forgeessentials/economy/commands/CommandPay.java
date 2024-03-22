package com.forgeessentials.economy.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandPay extends ForgeEssentialsCommandBuilder
{

    public CommandPay(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "pay";
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("player", EntityArgument.entity())
                .then(Commands.argument("amount", LongArgumentType.longArg(1))
                        .executes(CommandContext -> execute(CommandContext, "blank"))));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        UserIdent player = getIdent(EntityArgument.getPlayer(ctx, "player"));
        long amount = LongArgumentType.getLong(ctx, "amount");

        Wallet sender = APIRegistry.economy.getWallet(getIdent(getServerPlayer(ctx.getSource())));
        if (!sender.withdraw(amount))
        {
            ChatOutputHandler.chatError(ctx.getSource(), "You do not have enough %s in your wallet",
                    APIRegistry.economy.currency(2));
            return Command.SINGLE_SUCCESS;
        }

        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("You paid %s to %s. You now have %s",
                APIRegistry.economy.toString(amount), player.getUsernameOrUuid(), sender.toString()));

        Wallet receiver = APIRegistry.economy.getWallet(player);
        receiver.add(amount);
        ChatOutputHandler.chatConfirmation(player.getPlayerMP(),
                Translator.format("You were paid %s from %s. You now have %s", APIRegistry.economy.toString(amount),
                        getServerPlayer(ctx.getSource()).getDisplayName().getString(), receiver.toString()));
        return Command.SINGLE_SUCCESS;
    }
}
