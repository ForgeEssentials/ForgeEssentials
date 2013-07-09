package com.ForgeEssentials.economy.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.APIRegistry;
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
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player == null)
			{
				sender.sendChatToPlayer(args[0] + " not found!");
			}else if(player == sender){
				sender.sendChatToPlayer("You can't pay yourself!");
			}
			else
			{
				int amount = parseIntWithMin(sender, args[1], 0);
				if (APIRegistry.wallet.getWallet(sender.username) >= amount)
				{
					APIRegistry.wallet.removeFromWallet(amount, sender.username);
					APIRegistry.wallet.addToWallet(amount, player.username);
					OutputHandler.chatConfirmation(sender, "You have payed " + player.username + " " + amount + " " + APIRegistry.wallet.currency(amount));
					OutputHandler.chatConfirmation(player, "You have been payed " + amount + " " + APIRegistry.wallet.currency(amount) + " by " + sender.getCommandSenderName());
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
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (PlayerSelector.hasArguments(args[0]))
			{
				player = FunctionHelper.getPlayerForName(sender, args[0]);
			}
			if (player == null)
			{
				sender.sendChatToPlayer(args[0] + " not found!");
			}
			else
			{
				int amount = parseIntWithMin(sender, args[1], 0);
				APIRegistry.wallet.addToWallet(amount, player.username);
				OutputHandler.chatConfirmation(sender, "You have payed " + player.username + " " + amount + " " + APIRegistry.wallet.currency(amount));
				OutputHandler.chatConfirmation(player, "You have been payed " + amount + " " + APIRegistry.wallet.currency(amount) + " by " + sender.getCommandSenderName());
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
