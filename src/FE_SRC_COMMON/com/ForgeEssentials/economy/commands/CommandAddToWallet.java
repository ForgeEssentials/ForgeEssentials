package com.ForgeEssentials.economy.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.ChatUtils;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * @author HoBoS_TaCo
 */
public class CommandAddToWallet extends ForgeEssentialsCommandBase
{
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
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 2)
		{
			EntityPlayer player = FunctionHelper.getPlayerForName(sender, args[0]);
			int amountToAdd = Integer.parseInt(args[1]);

			if (player == null)
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOPLAYER));
			}
			else
			{
				APIRegistry.wallet.addToWallet(amountToAdd, player.username);

				if (sender != player)
				{
					ChatUtils.sendMessage(sender, amountToAdd + " " + APIRegistry.wallet.currency(amountToAdd) + Localization.get(Localization.wallet_ADD_TARGET));
				}
				ChatUtils.sendMessage(player, amountToAdd + " " + APIRegistry.wallet.currency(amountToAdd) + Localization.get(Localization.wallet_ADD_SELF));
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
			EntityPlayer player = FunctionHelper.getPlayerForName(sender, args[0]);
			int amountToAdd = Integer.parseInt(args[1]);

			if (player == null)
			{
				ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_NOPLAYER));
			}
			else
			{
				APIRegistry.wallet.addToWallet(amountToAdd, player.username);

				ChatUtils.sendMessage(sender, amountToAdd + " " + APIRegistry.wallet.currency(amountToAdd) + Localization.get(Localization.wallet_ADD_TARGET));
				ChatUtils.sendMessage(player, amountToAdd + " " + APIRegistry.wallet.currency(amountToAdd) + Localization.get(Localization.wallet_ADD_SELF));
			}
		}
		else
		{
			ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
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
