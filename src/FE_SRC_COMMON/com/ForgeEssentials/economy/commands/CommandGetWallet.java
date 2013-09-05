package com.ForgeEssentials.economy.commands;

import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandGetWallet extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "getwallet";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 1)
		{
			EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);

			if (player == null)
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOPLAYER));
			}
			else
			{
				if (sender != player)
				{
					ChatUtils.sendMessage(sender, player.username + Localization.get(Localization.wallet_GET_TARGET) + APIRegistry.wallet.getMoneyString(player.username));
				}
				ChatUtils.sendMessage(player, Localization.get(Localization.wallet_GET_SELF) + APIRegistry.wallet.getMoneyString(player.username));
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
		if (args.length == 1)
		{
			EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);

			if (player == null)
			{
				ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_NOPLAYER));
			}
			else
			{
				ChatUtils.sendMessage(sender, player.username + Localization.get(Localization.wallet_GET_TARGET) + APIRegistry.wallet.getMoneyString(player.username));
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
