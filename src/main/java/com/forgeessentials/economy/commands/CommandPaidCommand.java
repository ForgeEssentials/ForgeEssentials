package com.forgeessentials.economy.commands;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
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
        return "/paidcommand <player> <amount> <command [args]>";
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
     * Expected structure: "/paidcommand <player> <amount> <command [args]>"
     */
    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length >= 3)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                int amount = parseIntWithMin(sender, args[1], 0);
                if (!APIRegistry.economy.getWallet(player).withdraw(amount))
                    throw new TranslatedCommandException("You can't afford that!");
                args = Arrays.copyOfRange(args, 2, args.length);
                MinecraftServer.getServer().getCommandManager().executeCommand(sender, StringUtils.join(args, " "));
                OutputHandler.chatConfirmation(player, Translator.format("That cost you %d %s", amount, APIRegistry.economy.currency(amount)));
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
        else
            throw new TranslatedCommandException("Improper syntax. Please try this instead: <player> <amount> <command [args]>");
    }

}
