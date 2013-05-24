package com.ForgeEssentials.economy.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandRemoveWallet extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "removewallet";
	}

	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.asList("walletremove");
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 2)
		{
			EntityPlayer player = FunctionHelper.getPlayerForName(sender, args[0]);;
			int amountToSubtract = Integer.parseInt(args[1]);

			if (player == null)
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOPLAYER));
			}
			else
			{
				APIRegistry.wallet.removeFromWallet(amountToSubtract, player.username);

				if (sender != player)
				{
					sender.sendChatToPlayer(amountToSubtract + " " + APIRegistry.wallet.currency(amountToSubtract) + Localization.get(Localization.wallet_REMOVE_TARGET));
				}
				player.sendChatToPlayer(amountToSubtract + " " + APIRegistry.wallet.currency(amountToSubtract) + Localization.get(Localization.wallet_REMOVE_SELF));
			}
		}
		else
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 2)
		{
			EntityPlayer player = FunctionHelper.getPlayerForName(sender, args[0]);;
			int amountToSubtract = Integer.parseInt(args[1]);

			if (player == null)
			{
				sender.sendChatToPlayer(Localization.get(Localization.ERROR_NOPLAYER));
			}
			else
			{
				APIRegistry.wallet.removeFromWallet(amountToSubtract, player.username);

				sender.sendChatToPlayer(amountToSubtract + " " + APIRegistry.wallet.currency(amountToSubtract) + Localization.get(Localization.wallet_REMOVE_TARGET));
				player.sendChatToPlayer(amountToSubtract + " " + APIRegistry.wallet.currency(amountToSubtract) + Localization.get(Localization.wallet_REMOVE_SELF));
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

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}
}
