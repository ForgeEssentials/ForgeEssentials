package com.forgeessentials.economy.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandRequestPayment extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "requestpayment";
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
            else
            {
                int amount = parseIntWithMin(sender, args[1], 0);
                OutputHandler.chatConfirmation(sender,
                        "You have requested " + amount + APIRegistry.wallet.currency(amount) + " from " + player.getCommandSenderName() + ".");
                OutputHandler.chatConfirmation(player,
                        "You have been requested to play " + amount + APIRegistry.wallet.currency(amount) + " by " + player.getCommandSenderName() + ".");
            }
        }
        else
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: <player> <amountRequested>");
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length == 2)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player == null)
            {
                ChatUtils.sendMessage(sender, args[0] + " not found!");
            }
            else
            {
                int amount = parseIntWithMin(sender, args[1], 0);
                OutputHandler.chatConfirmation(sender,
                        "You have requested " + amount + APIRegistry.wallet.currency(amount) + " from " + player.getCommandSenderName() + ".");
                OutputHandler.chatConfirmation(player,
                        "You been requested to play " + amount + APIRegistry.wallet.currency(amount) + " by " + player.getCommandSenderName() + ".");
            }
        }
        else
        {
            ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: <player> <amountRequested>");
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
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

        return "/requestpayment <player> <amountRequested> Request a player to pay you a specified amount.";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.TRUE;
    }
}
