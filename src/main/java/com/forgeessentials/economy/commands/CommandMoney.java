package com.forgeessentials.economy.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;

public class CommandMoney extends ForgeEssentialsCommandBase
{
	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.asList("wallet");
	}

	@Override
	public String getCommandName()
	{
		return "money";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		OutputHandler.chatConfirmation(sender, "Your wallet contains: " + APIRegistry.wallet.getMoneyString(sender.username));
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
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/money Get the amound of money you have in your wallet.";
	}
}
