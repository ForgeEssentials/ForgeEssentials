package com.ForgeEssentials.economy.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.economy.EconManager;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandPay extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "pay";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 2)
		{
			EntityPlayerMP player = FunctionHelper.getPlayerForName(args[0]);
			if (player == null)
			{
				sender.sendChatToPlayer(args[0] + " not found!");
			}
			else
			{
				int amount = parseIntWithMin(sender, args[1], 0);
				if (EconManager.getWallet(sender.username) >= amount)
				{
					EconManager.removeFromWallet(amount, sender.username);
					EconManager.addToWallet(amount, sender.username);
					OutputHandler.chatConfirmation(sender, "You have payed " + player.username + " " + amount + " " + EconManager.currency(amount));
					OutputHandler.chatConfirmation(player, "You have been payed " + amount + " " + EconManager.currency(amount) + " by " + sender.getCommandSenderName());
				}
				else
				{
					OutputHandler.chatError(sender, "You can't afford that!!");
				}
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
			EntityPlayerMP player = FunctionHelper.getPlayerForName(args[0]);
			if (PlayerSelector.hasArguments(args[0]))
			{
				player = FunctionHelper.getPlayerForName(args[0]);
			}
			if (player == null)
			{
				sender.sendChatToPlayer(args[0] + " not found!");
			}
			else
			{
				int amount = parseIntWithMin(sender, args[1], 0);
				EconManager.addToWallet(amount, player.username);
				OutputHandler.chatConfirmation(sender, "You have payed " + player.username + " " + amount + " " + EconManager.currency(amount));
				OutputHandler.chatConfirmation(player, "You have been payed " + amount + " " + EconManager.currency(amount) + " by " + sender.getCommandSenderName());
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
