package com.ForgeEssentials.economy.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.economy.Wallet;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandSetWallet extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return Localization.get(Localization.WALLET_SET);
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 2)
		{
			EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			int amountToSet = Integer.parseInt(args[1]);

			if (player == null)
			{
				OutputHandler.chatError(sender, (Localization.get(Localization.ERROR_NOPLAYER)));
			}
			else
			{
				Wallet.setWallet(amountToSet, player);

				if (sender != player)
				{
					sender.sendChatToPlayer(Localization.get(Localization.WALLET_SET_TARGET) + Wallet.getWallet(player) + " "
							+ Wallet.currency(Wallet.getWallet(player)));
				}
				player.sendChatToPlayer(Localization.get(Localization.WALLET_SET_SELF) + Wallet.getWallet(player) + " "
						+ Wallet.currency(Wallet.getWallet(player)));
			}
		}
		else
		{
			OutputHandler.chatError(sender, (Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender)));
		}

	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 2)
		{
			EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			int amountToSet = Integer.parseInt(args[1]);

			if (player == null)
			{
				sender.sendChatToPlayer(Localization.get(Localization.ERROR_NOPLAYER));
			}
			else
			{
				Wallet.setWallet(amountToSet, player);

				sender.sendChatToPlayer(Localization.get(Localization.WALLET_SET_TARGET) + Wallet.getWallet(player) + " "
						+ Wallet.currency(Wallet.getWallet(player)));
				player.sendChatToPlayer(Localization.get(Localization.WALLET_SET_SELF) + Wallet.getWallet(player) + " "
						+ Wallet.currency(Wallet.getWallet(player)));
			}
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
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
