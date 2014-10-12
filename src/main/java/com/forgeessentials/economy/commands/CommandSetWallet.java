package com.forgeessentials.economy.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandSetWallet extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "setwallet";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().func_152612_a(args[0]);
            int amountToSet = Integer.parseInt(args[1]);

            if (player == null)
            {
                OutputHandler.chatError(sender, "Player does not exist, or is not online.");
            }
            else
            {
                APIRegistry.wallet.setWallet(amountToSet, player);

                OutputHandler.chatConfirmation(sender, "Wallet set to: " + APIRegistry.wallet.getMoneyString(player.getPersistentID()));
                OutputHandler.chatNotification(player, "Your wallet was set to " + APIRegistry.wallet.getMoneyString(player.getPersistentID()));
            }
        }
        else
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: <player> <amount>");
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

        return "/setwallet <player> <amount> Set a player's wallet to a certain amount.";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.OP;
    }

}
