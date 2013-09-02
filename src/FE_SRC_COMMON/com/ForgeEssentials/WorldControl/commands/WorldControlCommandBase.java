package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.tileentity.TileEntityCommandBlock;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.core.misc.FriendlyItemList;

public abstract class WorldControlCommandBase extends ForgeEssentialsCommandBase
{

	protected boolean	usesExtraSlash;

	/**
	 * @param doubleSlashCommand
	 */
	WorldControlCommandBase(boolean doubleSlashCommand)
	{
		usesExtraSlash = doubleSlashCommand;
	}

	@Override
	public final String getCommandName()
	{
		if (usesExtraSlash)
			return "/" + getName();
		else
			return getName();
	}

	public abstract String getName();

	@Override
	public String getSyntaxConsole()
	{
		// almost never called.
		return getCommandName();
	}

	@Override
	public String getInfoConsole()
	{
		// almost never called.
		return "";
	}

	@Override
	public String getSyntaxCommandBlock(TileEntityCommandBlock block)
	{
		// almost never called.
		return getCommandName();
	}

	@Override
	public void processCommandBlock(TileEntityCommandBlock block, String[] args)
	{
		// most probably never used.
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		ChatUtils.sendMessage(sender, "You cannot use the \"" + getCommandName() + "\" command from the console");
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.WorldControl.commands." + getName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return getListOfStringsFromIterableMatchingLastWord(args, FriendlyItemList.instance().getBlockList());
	}
}
