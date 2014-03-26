package com.forgeessentials.worldcontrol.commands;

//Depreciated
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.tileentity.TileEntityCommandBlock;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.FriendlyItemList;
import com.forgeessentials.util.ChatUtils;

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
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return getListOfStringsFromIterableMatchingLastWord(args, FriendlyItemList.instance().getBlockList());
	}
}
