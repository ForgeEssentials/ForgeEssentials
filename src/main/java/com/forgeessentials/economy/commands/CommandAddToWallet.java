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

/**
 * @author HoBoS_TaCo
 */
public class CommandAddToWallet extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "addtowallet";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Arrays.asList("walletadd");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 2)
        {
            EntityPlayer player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            int amountToAdd = Integer.parseInt(args[1]);

            if (player == null)
            {
                OutputHandler.chatError(sender, "Player %s does not exist, or is not online.");
            }
            else
            {
                APIRegistry.wallet.addToWallet(amountToAdd, player.getPersistentID());

                OutputHandler.chatConfirmation(sender, amountToAdd + " " + APIRegistry.wallet.currency(amountToAdd) + " added to wallet.");
                OutputHandler.chatConfirmation(player, amountToAdd + " " + APIRegistry.wallet.currency(amountToAdd) + " added to your wallet.");
            }
        }
        else
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: <player> <amounttoadd>");
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

        return "/walletadd <player> <amounttoadd> Add an amount to a wallet.";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.OP;
    }

}
