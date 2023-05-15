package com.forgeessentials.economy.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandPay extends ForgeEssentialsCommandBuilder
{

    public CommandPay(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "pay";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".pay";
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
                .then(Commands.argument("player", EntityArgument.entity())
                        .then(Commands.argument("amount", LongArgumentType.longArg(1))
                                .executes(CommandContext -> execute(CommandContext, "blank")
                                        )
                                )
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        UserIdent player = getIdent(EntityArgument.getPlayer(ctx, "player"));
        Long amount = LongArgumentType.getLong(ctx, "amount");

        Wallet sender = APIRegistry.economy.getWallet(getIdent(getServerPlayer(ctx.getSource())));
        if (!sender.withdraw(amount)) {
        	ChatOutputHandler.chatError(ctx.getSource(), "You do not have enough %s in your wallet", APIRegistry.economy.currency(2));
        	return Command.SINGLE_SUCCESS;
        }

        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("You paid %s to %s. You now have %s", 
        		APIRegistry.economy.toString(amount), player.getUsernameOrUuid(), sender.toString()));

        Wallet receiver = APIRegistry.economy.getWallet(player);
        receiver.add(amount);
        ChatOutputHandler.chatConfirmation(player.getPlayerMP(), Translator.format("You were paid %s from %s. You now have %s", 
                APIRegistry.economy.toString(amount), getServerPlayer(ctx.getSource()).getDisplayName().getString(), receiver.toString()));
        return Command.SINGLE_SUCCESS;
    }
}
