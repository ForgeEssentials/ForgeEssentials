package com.ForgeEssentials.economy.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.economy.WalletHandler;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandRequestPayment extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "requestpayment";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 2)
		{
			EntityPlayerMP player = PlayerSelector.matchOnePlayer(sender, args[0]);
			if (player == null)
			{
				sender.sendChatToPlayer(args[0] + " not found!");
			}
			else
			{
				int amount = parseIntWithMin(sender, args[1], 0);
				OutputHandler.chatConfirmation(sender, "You have requested " + amount + WalletHandler.currency(amount) + " from " + player.username + ".");
				OutputHandler.chatConfirmation(player, "You been requested to play " + amount + WalletHandler.currency(amount) + " by " + player.username + ".");
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
			EntityPlayerMP player = PlayerSelector.matchOnePlayer(sender, args[0]);
			if (player == null)
			{
				sender.sendChatToPlayer(args[0] + " not found!");
			}
			else
			{
				int amount = parseIntWithMin(sender, args[1], 0);
				OutputHandler.chatConfirmation(sender, "You have requested " + amount + WalletHandler.currency(amount) + " from " + player.username + ".");
				OutputHandler.chatConfirmation(player, "You been requested to play " + amount + WalletHandler.currency(amount) + " by " + player.username + ".");
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
		return false;
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
