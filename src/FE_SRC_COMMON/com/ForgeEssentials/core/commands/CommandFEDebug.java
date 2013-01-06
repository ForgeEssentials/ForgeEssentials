package com.ForgeEssentials.core.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.coremod.transformers.FEeventAdder;

public class CommandFEDebug extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "fedebug";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		debug(sender);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		debug(sender);
	}

	public void debug(ICommandSender sender)
	{
		try
		{
			if (FEeventAdder.addedBreak)
			{
				sender.sendChatToPlayer("The custom event 'PlayerBlockBreak' was added.");
			} else
			{
				sender.sendChatToPlayer("The custom event 'PlayerBlockBreak' was NOT added. Some functions might not work!");
				sender.sendChatToPlayer("The classname should be '"
						+ FEeventAdder.iiwmHM.get("className") + "' but is '"
						+ ItemInWorldManager.class.getName() + "'.");
			}
		} catch (Exception ex)
		{
			sender.sendChatToPlayer("Error finding custom event 'PlayerBlockBreak'");
		}

		try
		{
			if (FEeventAdder.addedPlace)
			{
				sender.sendChatToPlayer("The custom event 'PlayerBlockPlace' was added.");
			} else
			{
				sender.sendChatToPlayer("The custom event 'PlayerBlockPlace' was NOT added. Some functions might not work!");
				sender.sendChatToPlayer("The classname should be '"
						+ FEeventAdder.isHM.get("className") + "' but is '"
						+ ItemStack.class.getName() + "'.");
			}
		} catch (Exception ex)
		{
			sender.sendChatToPlayer("Error finding custom event 'PlayerBlockPlace'");
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return null;
	}

}
