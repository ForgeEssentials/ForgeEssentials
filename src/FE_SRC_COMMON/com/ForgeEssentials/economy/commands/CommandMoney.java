package com.ForgeEssentials.economy.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.economy.WalletHandler;
import com.ForgeEssentials.util.OutputHandler;

public class CommandMoney extends ForgeEssentialsCommandBase
{
	public CommandMoney()
	{
		aliasList.add("wallet");
	}
	
	@Override
	public String getCommandName()
	{
		return "money";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		OutputHandler.chatConfirmation(sender, "You have " + WalletHandler.getMoneyString(sender));
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
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
		// TODO Auto-generated method stub
		return null;
	}
}
