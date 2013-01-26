package com.ForgeEssentials.economy.commands;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.economy.Wallet;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * @author HoBoS_TaCo
 */
public class CommandAddToWallet extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return Localization.get(Localization.WALLET_ADD);
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 2)
		{
			EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			int amountToAdd = Integer.parseInt(args[1]);

			if (player == null)
			{
				OutputHandler.chatError(sender, (Localization.get(Localization.ERROR_NOPLAYER)));
			}
			else
			{
				Wallet.addToWallet(amountToAdd, player);

				if (sender != player)
				{
					sender.sendChatToPlayer(amountToAdd + " " + Wallet.currency(amountToAdd) + Localization.get(Localization.WALLET_ADD_TARGET));
				}
				player.sendChatToPlayer(amountToAdd + " " + Wallet.currency(amountToAdd) + Localization.get(Localization.WALLET_ADD_SELF));
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
			int amountToAdd = Integer.parseInt(args[1]);

			if (player == null)
			{
				sender.sendChatToPlayer(Localization.get(Localization.ERROR_NOPLAYER));
			}
			else
			{
				Wallet.addToWallet(amountToAdd, player);

				sender.sendChatToPlayer(amountToAdd + " " + Wallet.currency(amountToAdd) + Localization.get(Localization.WALLET_ADD_TARGET));
				player.sendChatToPlayer(amountToAdd + " " + Wallet.currency(amountToAdd) + Localization.get(Localization.WALLET_ADD_SELF));
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
