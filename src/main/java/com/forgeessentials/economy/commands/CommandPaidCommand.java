package com.forgeessentials.economy.commands;

import java.util.Arrays;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException.InvalidSyntaxException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandPaidCommand extends ForgeEssentialsCommandBase
{
    @Override
    public String getPrimaryAlias()
    {
        return "paidcommand";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "pc", "pcmd" };
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".paidcommand";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.NONE;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/paidcommand <player> <amount> <command...>";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    /*
     * Expected structure: "/paidcommand <player> <amount> <command...>"
     */
    @Override
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 3)
            throw new InvalidSyntaxException(getUsage(sender));

        UserIdent ident = UserIdent.get(args[0], sender);
        if (!ident.hasPlayer())
            throw new PlayerNotFoundException("commands.generic.player.notFound");

        int amount = parseInt(args[1], 0, Integer.MAX_VALUE);
        Wallet wallet = APIRegistry.economy.getWallet(ident);
        if (!wallet.withdraw(amount))
        {
            ChatOutputHandler.chatError(ident.getPlayerMP(), Translator.translate("You can't afford that"));
            return;
        }

        args = Arrays.copyOfRange(args, 2, args.length);
        server.getCommandManager().executeCommand(new DoAsCommandSender(ModuleEconomy.ECONOMY_IDENT, ident.getPlayerMP()), StringUtils.join(args, " "));

        ChatOutputHandler.chatConfirmation(ident.getPlayerMP(), Translator.format("That cost you %s", APIRegistry.economy.toString(amount)));
        ModuleEconomy.confirmNewWalletAmount(ident, wallet);
    }

}
