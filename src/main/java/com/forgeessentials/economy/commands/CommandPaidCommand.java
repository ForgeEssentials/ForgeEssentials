package com.forgeessentials.economy.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
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
import com.forgeessentials.economy.ModuleEconomy.CantAffordException;
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
        return null;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return null;
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

    @Override
    public boolean canPlayerUseCommand(EntityPlayer player)
    {
        return false;
    }

    /*
     * Expected structure: "/paidcommand <player> <amount> <command...>"
     */
    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length < 3)
            throw new InvalidSyntaxException(getCommandUsage(sender));

        UserIdent ident = new UserIdent(args[0], sender);
        if (!ident.hasUUID())
            throw new PlayerNotFoundException();

        int amount = parseIntWithMin(sender, args[1], 0);
        Wallet wallet = APIRegistry.economy.getWallet(ident);
        if (!wallet.withdraw(amount))
            throw new CantAffordException();

        args = Arrays.copyOfRange(args, 2, args.length);
        MinecraftServer.getServer().getCommandManager().executeCommand(sender, StringUtils.join(args, " "));

        OutputHandler.chatConfirmation(sender, Translator.format("That cost you %s", APIRegistry.economy.toString(amount)));
        ModuleEconomy.confirmNewWalletAmount(ident, wallet);
    }

}
