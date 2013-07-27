package com.ForgeEssentials.core.commands;

import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.core.preloader.Data;
import com.ForgeEssentials.core.preloader.asm.FEeventAdder;

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
			if (FEeventAdder.addedBreak) {
                ChatUtils.sendMessage(sender, "The custom event 'PlayerBlockBreak' was added.");
            }
			else {
                ChatUtils.sendMessage(sender, "The custom event 'PlayerBlockBreak' was NOT added. Some functions might not work!");
                ChatUtils.sendMessage(sender, "The classname should be '" + Data.ISob.get("className") + "' but is '" + ItemInWorldManager.class.getName() + "'.");
            }
		}
		catch (Exception ex) {
            ChatUtils.sendMessage(sender, "Error finding custom event 'PlayerBlockBreak'");
        }

		try
		{
			if (FEeventAdder.addedPlace) {
                ChatUtils.sendMessage(sender, "The custom event 'PlayerBlockPlace' was added.");
            }
			else {
                ChatUtils.sendMessage(sender, "The custom event 'PlayerBlockPlace' was NOT added. Some functions might not work!");
                ChatUtils.sendMessage(sender, "The classname should be '" + Data.ISob.get("className") + "' but is '" + ItemStack.class.getName() + "'.");
            }
		}
		catch (Exception ex) {
            ChatUtils.sendMessage(sender, "Error finding custom event 'PlayerBlockPlace'");
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
		return "ForgeEssentials.CoreCommands." + getCommandName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}
	
	@Override
	public String getSyntaxConsole()
	{
		return "";
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "";
	}

	@Override
	public String getInfoConsole()
	{
		return "Use if you need help with FE related stuff";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Use if you need help with FE related stuff";
	}
}
