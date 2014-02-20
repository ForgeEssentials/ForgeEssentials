package com.forgeessentials.economy.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.Localization;
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
		OutputHandler.chatConfirmation(sender, Localization.get(Localization.wallet_GET_SELF) + APIRegistry.wallet.getMoneyString(sender.username));
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
		return null;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
