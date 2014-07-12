package com.forgeessentials.economy.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

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
                ChatUtils.sendMessage(sender, "Player does not exist, or is not online.");
            }
            else
            {
                APIRegistry.wallet.setWallet(amountToSet, player);

                ChatUtils.sendMessage(sender, "Wallet set to: " + APIRegistry.wallet.getMoneyString(player.getPersistentID()));
                ChatUtils.sendMessage(player, "Your wallet was set to " + APIRegistry.wallet.getMoneyString(player.getPersistentID()));
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
    public String getCommandUsage(ICommandSender sender)
    {

        return "/setwallet <player> <amount> Set a player's wallet to a certain amount.";
    }

    @Override
    public RegGroup getReggroup()
    {

        return RegGroup.OWNERS;
    }

}
