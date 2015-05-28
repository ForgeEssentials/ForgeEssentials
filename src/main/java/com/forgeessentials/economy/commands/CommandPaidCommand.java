package com.forgeessentials.economy.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException.InvalidSyntaxException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.DoAsConsoleCommandSender;
import com.forgeessentials.util.OutputHandler;

public class CommandPaidCommand extends ForgeEssentialsCommandBase
{
    @Override
    public String getCommandName()
    {
        return "paidcommand";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Arrays.asList("pc", "pcmd");
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".paidcommand";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.FALSE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
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
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length < 3)
            throw new InvalidSyntaxException(getCommandUsage(sender));

        UserIdent ident = UserIdent.get(args[0], sender);
        if (!ident.hasPlayer())
            throw new PlayerNotFoundException();

        int amount = parseIntWithMin(sender, args[1], 0);
        Wallet wallet = APIRegistry.economy.getWallet(ident);
        if (!wallet.withdraw(amount))
        {
            OutputHandler.chatError(ident.getPlayerMP(), Translator.translate("You can't afford that"));
            return;
        }

        args = Arrays.copyOfRange(args, 2, args.length);
        MinecraftServer.getServer().getCommandManager().executeCommand(new DoAsConsoleCommandSender(ident.getPlayerMP()), StringUtils.join(args, " "));

        OutputHandler.chatConfirmation(ident.getPlayerMP(), Translator.format("That cost you %s", APIRegistry.economy.toString(amount)));
        ModuleEconomy.confirmNewWalletAmount(ident, wallet);
    }

}
