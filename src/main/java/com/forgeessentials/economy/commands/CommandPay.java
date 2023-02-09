package com.forgeessentials.economy.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (arguments.isEmpty())
            throw new TranslatedCommandException("Player needed");
        UserIdent player = arguments.parsePlayer(true, false);

        if (arguments.isEmpty())
            throw new TranslatedCommandException("Missing value");
        Long amount = ServerUtil.tryParseLong(arguments.remove());
        if (amount == null)
            throw new TranslatedCommandException("Invalid number");
        if (amount < 1)
            throw new TranslatedCommandException("Invalid number");

        if (arguments.isTabCompletion)
            return;

        Wallet sender = APIRegistry.economy.getWallet(arguments.ident);
        if (!sender.withdraw(amount))
            throw new TranslatedCommandException("You do not have enough %s in your wallet", APIRegistry.economy.currency(2));
        arguments.confirm(Translator.format("You paid %s to %s. You now have %s", //
                APIRegistry.economy.toString(amount), player.getUsernameOrUuid(), sender.toString()));

        Wallet receiver = APIRegistry.economy.getWallet(player);
        receiver.add(amount);
        ChatOutputHandler.chatConfirmation(player.getPlayerMP(), Translator.format("You were paid %s from %s. You now have %s", //
                APIRegistry.economy.toString(amount), arguments.sender.getTextName(), receiver.toString()));
    }
}
