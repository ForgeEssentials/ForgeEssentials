package com.forgeessentials.economy.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandPay extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "pay";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 2)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player == null)
            {
                ChatUtils.sendMessage(sender, args[0] + " not found!");
            }
            else if (player == sender)
            {
                ChatUtils.sendMessage(sender, "You can't pay yourself!");
            }
            else
            {
                int amount = parseIntWithMin(sender, args[1], 0);
                if (APIRegistry.wallet.getWallet(sender.getPersistentID()) >= amount)
                {
                    APIRegistry.wallet.removeFromWallet(amount, sender.getPersistentID());
                    APIRegistry.wallet.addToWallet(amount, player.getPersistentID());
                    OutputHandler.chatConfirmation(sender,
                            "You have payed " + player.getCommandSenderName() + " " + amount + " " + APIRegistry.wallet.currency(amount));
                    OutputHandler.chatConfirmation(player,
                            "You have been payed " + amount + " " + APIRegistry.wallet.currency(amount) + " by " + sender.getCommandSenderName());
                }
                else
                {
                    OutputHandler.chatError(sender, "You can't afford that!!");
                }
            }
        }
        else
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: <player> <amount>");
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length == 2)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (PlayerSelector.hasArguments(args[0]))
            {
                player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            }
            if (player == null)
            {
                ChatUtils.sendMessage(sender, args[0] + " not found!");
            }
            else
            {
                int amount = parseIntWithMin(sender, args[1], 0);
                APIRegistry.wallet.addToWallet(amount, player.getPersistentID());
                OutputHandler
                        .chatConfirmation(sender, "You have payed " + player.getCommandSenderName() + " " + amount + " " + APIRegistry.wallet.currency(amount));
                OutputHandler.chatConfirmation(player,
                        "You have been payed " + amount + " " + APIRegistry.wallet.currency(amount) + " by " + sender.getCommandSenderName());
            }
        }
        else
        {
            ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: <player> <amount>");
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

        return "/pay <player> <amount> Pay another player an amount of money";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.TRUE;
    }
}
