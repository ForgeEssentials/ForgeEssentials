package com.forgeessentials.economy.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

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
            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
            if (player == null)
            {
                ChatUtils.sendMessage(sender, args[0] + " not found!");
            }
            else
            {
                int amount = parseIntWithMin(sender, args[1], 0);
                OutputHandler.chatConfirmation(sender, "You have requested " + amount + APIRegistry.wallet.currency(amount) + " from " + player.username + ".");
                OutputHandler.chatConfirmation(player,
                        "You been requested to play " + amount + APIRegistry.wallet.currency(amount) + " by " + player.username + ".");
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
            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
            if (player == null)
            {
                ChatUtils.sendMessage(sender, args[0] + " not found!");
            }
            else
            {
                int amount = parseIntWithMin(sender, args[1], 0);
                OutputHandler.chatConfirmation(sender, "You have requested " + amount + APIRegistry.wallet.currency(amount) + " from " + player.username + ".");
                OutputHandler.chatConfirmation(player,
                        "You been requested to play " + amount + APIRegistry.wallet.currency(amount) + " by " + player.username + ".");
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
    public String getCommandPerm()
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
    public int compareTo(Object o)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        // TODO Auto-generated method stub
        return "/requestpayment <player> <amountRequested> Request a player to pay you a specified amount.";
    }

    @Override
    public RegGroup getReggroup()
    {
        // TODO Auto-generated method stub
        return RegGroup.MEMBERS;
    }
}
