package com.ForgeEssentials.economy.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.economy.EconManager;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
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
			EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			int amountToAdd = Integer.parseInt(args[1]);

			if (player == null)
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOPLAYER));
			}
			else
			{
				EconManager.addToWallet(amountToAdd, player.username);

				if (sender != player)
				{
					sender.sendChatToPlayer(amountToAdd + " " + EconManager.currency(amountToAdd) + Localization.get(Localization.wallet_ADD_TARGET));
				}
				player.sendChatToPlayer(amountToAdd + " " + EconManager.currency(amountToAdd) + Localization.get(Localization.wallet_ADD_SELF));
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
			EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			int amountToAdd = Integer.parseInt(args[1]);

			if (player == null)
			{
				sender.sendChatToPlayer(Localization.get(Localization.ERROR_NOPLAYER));
			}
			else
			{
				EconManager.addToWallet(amountToAdd, player.username);

				sender.sendChatToPlayer(amountToAdd + " " + EconManager.currency(amountToAdd) + Localization.get(Localization.wallet_ADD_TARGET));
				player.sendChatToPlayer(amountToAdd + " " + EconManager.currency(amountToAdd) + Localization.get(Localization.wallet_ADD_SELF));
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
