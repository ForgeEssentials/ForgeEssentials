package com.ForgeEssentials.economy.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.economy.Wallet;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandRemoveWallet extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return Localization.get(Localization.WALLET_REMOVE);
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 2)
		{
			EntityPlayer player = FMLCommonHandler.instance()
					.getSidedDelegate().getServer().getConfigurationManager()
					.getPlayerForUsername(args[0]);
			int amountToSubtract = Integer.parseInt(args[1]);

			if (player == null)
			{
				OutputHandler.chatError(sender,
						(Localization.get(Localization.ERROR_NOPLAYER)));
			} else
			{
				Wallet.removeFromWallet(amountToSubtract, player);

				if (sender != player)
				{
					sender.sendChatToPlayer(amountToSubtract
							+ " "
							+ Wallet.currency(amountToSubtract)
							+ Localization
									.get(Localization.WALLET_REMOVE_TARGET));
				}
				player.sendChatToPlayer(amountToSubtract + " "
						+ Wallet.currency(amountToSubtract)
						+ Localization.get(Localization.WALLET_REMOVE_SELF));
			}
		} else
		{
			OutputHandler
					.chatError(
							sender,
							(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender)));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 2)
		{
			EntityPlayer player = FMLCommonHandler.instance()
					.getSidedDelegate().getServer().getConfigurationManager()
					.getPlayerForUsername(args[0]);
			int amountToSubtract = Integer.parseInt(args[1]);

			if (player == null)
			{
				sender.sendChatToPlayer(Localization
						.get(Localization.ERROR_NOPLAYER));
			} else
			{
				Wallet.removeFromWallet(amountToSubtract, player);

				sender.sendChatToPlayer(amountToSubtract + " "
						+ Wallet.currency(amountToSubtract)
						+ Localization.get(Localization.WALLET_REMOVE_TARGET));
				player.sendChatToPlayer(amountToSubtract + " "
						+ Wallet.currency(amountToSubtract)
						+ Localization.get(Localization.WALLET_REMOVE_SELF));
			}
		} else
		{
			sender.sendChatToPlayer(Localization
					.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
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
}
