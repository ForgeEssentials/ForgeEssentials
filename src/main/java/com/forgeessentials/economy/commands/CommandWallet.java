package com.forgeessentials.economy.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.ServerUtil;

public class CommandWallet extends ParserCommandBase
{

    public static final String PERM = ModuleEconomy.PERM_COMMAND + ".wallet";
    public static final String PERM_OTHERS = PERM + ".others";
    public static final String PERM_MODIFY = PERM + ".modify";

    @Override
    public String getPrimaryAlias()
    {
        return "wallet";
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_OTHERS, DefaultPermissionLevel.OP, "Allows viewing other player's wallets");
        APIRegistry.perms.registerPermission(PERM_MODIFY, DefaultPermissionLevel.OP, "Allows modifying wallets");
    }
    
    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/wallet: Check your wallet";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            if (!arguments.hasPlayer())
                throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
            arguments.confirm(Translator.format("Your wallet contains %s", APIRegistry.economy.getWallet(arguments.ident).toString()));
            return;
        }

        UserIdent player = arguments.parsePlayer(true, false);
        if (!player.equals(arguments.ident))
            arguments.checkPermission(PERM_OTHERS);
        
        Wallet wallet = APIRegistry.economy.getWallet(player);
        if (arguments.isEmpty())
        {
            arguments.confirm(Translator.format("Wallet of %s contains %s", player.getUsernameOrUuid(), wallet.toString()));
            return;
        }
        
        arguments.tabComplete(new String[] { "set", "add", "remove" });
        String subCommand = arguments.remove().toLowerCase();

        arguments.checkPermission(PERM_MODIFY);

        if (arguments.isEmpty())
            throw new TranslatedCommandException("Missing value");
        Long amount = ServerUtil.tryParseLong(arguments.remove());
        if (amount == null)
            throw new TranslatedCommandException("Invalid number");

        switch (subCommand)
        {
        case "set":
            wallet.set(amount);
            arguments.confirm(Translator.format("Set wallet of %s to %s", player.getUsernameOrUuid(), wallet.toString()));
            break;
        case "add":
            wallet.add(amount);
            arguments.confirm(Translator.format("Added %s to %s's wallet. It now contains %s", //
                    APIRegistry.economy.toString(amount), player.getUsernameOrUuid(), wallet.toString()));
            break;
        case "remove":
            if (!wallet.withdraw(amount))
                throw new TranslatedCommandException("Player %s does not have enough %s in his wallet", //
                        player.getUsernameOrUuid(), APIRegistry.economy.currency(2));
            arguments.confirm(Translator.format("Removed %s from %s's wallet. It now contains %s", //
                    APIRegistry.economy.toString(amount), player.getUsernameOrUuid(), wallet.toString()));
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCommand);
        }
    }

}
