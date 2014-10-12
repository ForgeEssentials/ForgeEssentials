package com.forgeessentials.economy.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandRemoveWallet extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "removewallet";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Arrays.asList("walletremove");
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 2)
        {
            EntityPlayer player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            int amountToSubtract = Integer.parseInt(args[1]);

            if (player == null)
            {
                OutputHandler.chatError(sender, "Player does not exist, or is not online.");
            }
            else
            {
                APIRegistry.wallet.removeFromWallet(amountToSubtract, player.getPersistentID());

                if (sender != player)
                {
                    OutputHandler.chatConfirmation(sender, amountToSubtract + " " + APIRegistry.wallet.currency(amountToSubtract) + " was removed from the wallet.");
                }
                OutputHandler.chatNotification(player, amountToSubtract + " " + APIRegistry.wallet.currency(amountToSubtract) + " was removed from your wallet.");
            }
        }
        else
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: <player> <amounttoremove>");
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length == 2)
        {
            EntityPlayer player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            int amountToSubtract = Integer.parseInt(args[1]);

            if (player == null)
            {
                OutputHandler.chatError(sender, "Player does not exist, or is not online.");
            }
            else
            {
                APIRegistry.wallet.removeFromWallet(amountToSubtract, player.getPersistentID());

                OutputHandler.chatConfirmation(sender, amountToSubtract + " " + APIRegistry.wallet.currency(amountToSubtract) + " was removed from the wallet.");
                OutputHandler.chatNotification(player, amountToSubtract + " " + APIRegistry.wallet.currency(amountToSubtract) + " was removed from your wallet.");
            }
        }
        else
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: <player> <amounttoremove>");
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.economy." + getCommandName();
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/removewallet <player> <amounttoremove> Remove an amount of money from a player's wallet.";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.OP;
    }
}
