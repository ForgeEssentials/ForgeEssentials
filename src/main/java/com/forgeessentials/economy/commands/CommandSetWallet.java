package com.forgeessentials.economy.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandSetWallet extends ForgeEssentialsCommandBase
{
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
			EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			int amountToSet = Integer.parseInt(args[1]);

			if (player == null)
			{
				ChatUtils.sendMessage(sender, "Player does not exist, or is not online.");
			}
			else
			{
				APIRegistry.wallet.setWallet(amountToSet, player);

				ChatUtils.sendMessage(sender, "Wallet set to: " + APIRegistry.wallet.getMoneyString(player.username));
				ChatUtils.sendMessage(player, "Your wallet was set to " + APIRegistry.wallet.getMoneyString(player.username));
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
		return "ForgeEssentials.Economy." + getCommandName();
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/setwallet <player> <amount> Set a player's wallet to a certain amount.";
	}

}
